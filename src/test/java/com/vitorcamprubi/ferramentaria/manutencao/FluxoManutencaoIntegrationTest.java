package com.vitorcamprubi.ferramentaria.manutencao;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/**
 * Testa o fluxo completo pela API: controller -> facade -> strategy/chain -> banco H2.
 * O {@code @Transactional} desfaz as alterações no fim de cada teste, então um teste
 * não interfere no outro.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FluxoManutencaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void preventivaCompletaZeraOsCiclosEDevolveOMoldeParaAProducao() throws Exception {
        long moldeId = cadastrarMolde("MD-9001", 100_000);
        registrarCiclos(moldeId, 85_000).andExpect(status().isOk());

        long ordemId = idDe(abrirOrdem(moldeId, "PREVENTIVA", false)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.prioridade").value("MEDIA"))
                .andExpect(jsonPath("$.status").value("ABERTA")));

        mockMvc.perform(get("/moldes/{id}", moldeId))
                .andExpect(jsonPath("$.status").value("EM_MANUTENCAO"));

        concluir(ordemId, true, true, true, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.concluidaEm").isNotEmpty());

        mockMvc.perform(get("/moldes/{id}", moldeId))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.ciclosDesdePreventiva").value(0));
    }

    @Test
    void corretivaQueParouALinhaEhCriticaEExigeCausaRaizParaLiberar() throws Exception {
        long moldeId = cadastrarMolde("MD-9002", 100_000);
        registrarCiclos(moldeId, 40_000);

        long ordemId = idDe(abrirOrdem(moldeId, "CORRETIVA", true)
                .andExpect(jsonPath("$.prioridade").value("CRITICA")));

        concluir(ordemId, true, true, true, "")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.etapa").value("Causa raiz"));

        concluir(ordemId, true, true, true, "Pino extrator quebrado por fadiga")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.causaRaiz").value("Pino extrator quebrado por fadiga"));

        // Corretiva não substitui a preventiva: o contador de ciclos continua.
        mockMvc.perform(get("/moldes/{id}", moldeId))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.ciclosDesdePreventiva").value(40_000));
    }

    @Test
    void checklistReprovadoMantemOMoldeEmManutencao() throws Exception {
        long moldeId = cadastrarMolde("MD-9003", 50_000);
        long ordemId = idDe(abrirOrdem(moldeId, "MELHORIA", false));

        concluir(ordemId, true, true, false, null)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Liberação do molde recusada"))
                .andExpect(jsonPath("$.etapa").value("Try-out"));

        mockMvc.perform(get("/ordens/{id}", ordemId))
                .andExpect(jsonPath("$.status").value("ABERTA"));
        mockMvc.perform(get("/moldes/{id}", moldeId))
                .andExpect(jsonPath("$.status").value("EM_MANUTENCAO"));
    }

    @Test
    void moldeEmManutencaoNaoAceitaSegundaOrdemNemCiclosDeProducao() throws Exception {
        long moldeId = cadastrarMolde("MD-9004", 50_000);
        abrirOrdem(moldeId, "CORRETIVA", false).andExpect(status().isCreated());

        abrirOrdem(moldeId, "PREVENTIVA", false)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("O molde MD-9004 já está em manutenção com uma ordem aberta"));

        registrarCiclos(moldeId, 1_000).andExpect(status().isConflict());
    }

    @Test
    void ordemJaConcluidaNaoPodeSerConcluidaDeNovo() throws Exception {
        long moldeId = cadastrarMolde("MD-9005", 50_000);
        long ordemId = idDe(abrirOrdem(moldeId, "PREVENTIVA", false));
        concluir(ordemId, true, true, true, null).andExpect(status().isOk());

        concluir(ordemId, true, true, true, null).andExpect(status().isConflict());
    }

    @Test
    void filaDeOrdensAbertasComecaPelaMaisUrgente() throws Exception {
        long melhoria = idDe(abrirOrdem(cadastrarMolde("MD-9101", 50_000), "MELHORIA", false));
        long corretivaCritica = idDe(abrirOrdem(cadastrarMolde("MD-9102", 50_000), "CORRETIVA", true));
        long corretivaAlta = idDe(abrirOrdem(cadastrarMolde("MD-9103", 50_000), "CORRETIVA", false));

        // Os moldes do data.sql não têm ordens, então a fila contém só as três abertas aqui.
        mockMvc.perform(get("/ordens").param("status", "ABERTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(contains((int) corretivaCritica, (int) corretivaAlta, (int) melhoria)))
                .andExpect(jsonPath("$[*].prioridade").value(contains("CRITICA", "ALTA", "BAIXA")));
    }

    @Test
    void codigoDeMoldeDuplicadoRetornaConflito() throws Exception {
        cadastrarMolde("MD-9006", 50_000);

        mockMvc.perform(post("/moldes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"codigo": "MD-9006", "descricao": "Outro molde", "cavidades": 2, "limiteCiclosPreventiva": 10000}
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void dadosInvalidosRetornam400ComOsCamposComErro() throws Exception {
        mockMvc.perform(post("/moldes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"codigo": "", "descricao": "Molde sem código", "cavidades": 0, "limiteCiclosPreventiva": 10000}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.codigo").exists())
                .andExpect(jsonPath("$.campos.cavidades").exists());
    }

    @Test
    void tipoDeManutencaoInexistenteRetorna400() throws Exception {
        long moldeId = cadastrarMolde("MD-9007", 50_000);

        abrirOrdem(moldeId, "LIMPEZA", false).andExpect(status().isBadRequest());
    }

    @Test
    void ordemParaMoldeInexistenteRetorna404() throws Exception {
        abrirOrdem(999_999L, "CORRETIVA", true).andExpect(status().isNotFound());
    }

    // ----- auxiliares -----

    private long cadastrarMolde(String codigo, long limiteCiclos) throws Exception {
        return idDe(mockMvc.perform(post("/moldes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"codigo": "%s", "descricao": "Molde de teste", "cavidades": 4, "limiteCiclosPreventiva": %d}
                        """.formatted(codigo, limiteCiclos)))
                .andExpect(status().isCreated()));
    }

    private ResultActions registrarCiclos(long moldeId, long quantidade) throws Exception {
        return mockMvc.perform(patch("/moldes/{id}/ciclos", moldeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantidade\": %d}".formatted(quantidade)));
    }

    private ResultActions abrirOrdem(long moldeId, String tipo, boolean paradaDeProducao) throws Exception {
        return mockMvc.perform(post("/ordens").contentType(MediaType.APPLICATION_JSON).content("""
                {"moldeId": %d, "tipo": "%s", "descricaoProblema": "Descrição de teste", "paradaDeProducao": %b}
                """.formatted(moldeId, tipo, paradaDeProducao)));
    }

    private ResultActions concluir(long ordemId, boolean refrigeracao, boolean extracao, boolean tryOut,
                                   String causaRaiz) throws Exception {
        String causa = causaRaiz == null ? "null" : "\"" + causaRaiz + "\"";
        return mockMvc.perform(post("/ordens/{id}/conclusao", ordemId).contentType(MediaType.APPLICATION_JSON).content("""
                {"refrigeracaoTestada": %b, "extracaoTestada": %b, "tryOutAprovado": %b, "causaRaiz": %s}
                """.formatted(refrigeracao, extracao, tryOut, causa)));
    }

    private long idDe(ResultActions resultado) throws Exception {
        String json = resultado.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }
}
