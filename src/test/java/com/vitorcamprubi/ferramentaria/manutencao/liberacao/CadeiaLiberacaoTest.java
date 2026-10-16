package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.vitorcamprubi.ferramentaria.common.exception.LiberacaoRecusadaException;
import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;
import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

class CadeiaLiberacaoTest {

    private final EtapaLiberacao cadeia = new LiberacaoConfig().cadeiaLiberacao();

    @Test
    void liberaQuandoTodasAsEtapasAprovam() {
        OrdemManutencao corretiva = ordem(TipoManutencao.CORRETIVA);
        ChecklistLiberacao checklist = new ChecklistLiberacao(true, true, true, "Pino extrator quebrado por fadiga");

        assertThatCode(() -> cadeia.validar(corretiva, checklist)).doesNotThrowAnyException();
    }

    @Test
    void paraNaPrimeiraEtapaQueReprova() {
        // Refrigeração e try-out reprovados: a corrente para logo no primeiro elo.
        ChecklistLiberacao checklist = new ChecklistLiberacao(false, true, false, null);

        assertThatThrownBy(() -> cadeia.validar(ordem(TipoManutencao.PREVENTIVA), checklist))
                .isInstanceOf(LiberacaoRecusadaException.class)
                .extracting("etapa").isEqualTo("Refrigeração");
    }

    @Test
    void reprovaQuandoAExtracaoNaoFoiTestada() {
        ChecklistLiberacao checklist = new ChecklistLiberacao(true, false, true, null);

        assertThatThrownBy(() -> cadeia.validar(ordem(TipoManutencao.PREVENTIVA), checklist))
                .isInstanceOf(LiberacaoRecusadaException.class)
                .extracting("etapa").isEqualTo("Extração");
    }

    @Test
    void corretivaSemCausaRaizNaoELiberada() {
        ChecklistLiberacao checklist = new ChecklistLiberacao(true, true, true, "   ");

        assertThatThrownBy(() -> cadeia.validar(ordem(TipoManutencao.CORRETIVA), checklist))
                .isInstanceOf(LiberacaoRecusadaException.class)
                .extracting("etapa").isEqualTo("Causa raiz");
    }

    @Test
    void preventivaNaoPrecisaDeCausaRaiz() {
        ChecklistLiberacao checklist = new ChecklistLiberacao(true, true, true, null);

        assertThatCode(() -> cadeia.validar(ordem(TipoManutencao.PREVENTIVA), checklist)).doesNotThrowAnyException();
    }

    @Test
    void tryOutReprovadoSeguraOMoldeNaFerramentaria() {
        ChecklistLiberacao checklist = new ChecklistLiberacao(true, true, false, null);

        assertThatThrownBy(() -> cadeia.validar(ordem(TipoManutencao.MELHORIA), checklist))
                .isInstanceOf(LiberacaoRecusadaException.class)
                .extracting("etapa").isEqualTo("Try-out");
    }

    private OrdemManutencao ordem(TipoManutencao tipo) {
        Molde molde = new Molde("MD-TESTE", "Molde de teste", 4, 100_000);
        return new OrdemManutencao(molde, tipo, "Descrição de teste", false, Prioridade.MEDIA);
    }
}
