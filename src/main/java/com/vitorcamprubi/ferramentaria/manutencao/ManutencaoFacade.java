package com.vitorcamprubi.ferramentaria.manutencao;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitorcamprubi.ferramentaria.common.exception.RecursoNaoEncontradoException;
import com.vitorcamprubi.ferramentaria.common.exception.RegraNegocioException;
import com.vitorcamprubi.ferramentaria.manutencao.dto.AberturaOrdemRequest;
import com.vitorcamprubi.ferramentaria.manutencao.dto.OrdemResponse;
import com.vitorcamprubi.ferramentaria.manutencao.liberacao.ChecklistLiberacao;
import com.vitorcamprubi.ferramentaria.manutencao.liberacao.EtapaLiberacao;
import com.vitorcamprubi.ferramentaria.manutencao.prioridade.CalculadoraPrioridade;
import com.vitorcamprubi.ferramentaria.molde.Molde;
import com.vitorcamprubi.ferramentaria.molde.MoldeRepository;

/**
 * <b>Facade</b>: é a única porta de entrada para o fluxo de manutenção.
 * <p>
 * Abrir ou concluir uma ordem envolve vários subsistemas: buscar e travar o molde,
 * calcular a prioridade (Strategy), rodar o checklist de liberação (Chain of
 * Responsibility) e persistir tudo numa transação. O controller não precisa conhecer
 * nenhuma dessas peças: ele chama {@code abrirOrdem} ou {@code concluirOrdem} e pronto.
 */
@Service
public class ManutencaoFacade {

    /** Fila da ferramentaria: mais urgente primeiro e, no empate, quem abriu antes. */
    private static final Comparator<OrdemManutencao> ORDEM_DA_FILA =
            Comparator.comparing(OrdemManutencao::getPrioridade).reversed()
                    .thenComparing(OrdemManutencao::getAbertaEm);

    private final MoldeRepository moldeRepository;
    private final OrdemManutencaoRepository ordemRepository;
    private final CalculadoraPrioridade calculadoraPrioridade;
    private final EtapaLiberacao cadeiaLiberacao;

    public ManutencaoFacade(MoldeRepository moldeRepository,
                            OrdemManutencaoRepository ordemRepository,
                            CalculadoraPrioridade calculadoraPrioridade,
                            EtapaLiberacao cadeiaLiberacao) {
        this.moldeRepository = moldeRepository;
        this.ordemRepository = ordemRepository;
        this.calculadoraPrioridade = calculadoraPrioridade;
        this.cadeiaLiberacao = cadeiaLiberacao;
    }

    @Transactional
    public OrdemResponse abrirOrdem(AberturaOrdemRequest request) {
        Molde molde = moldeRepository.findById(request.moldeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Molde %d não encontrado".formatted(request.moldeId())));

        // A prioridade é calculada antes de mudar o status, com os ciclos atuais do molde.
        Prioridade prioridade = calculadoraPrioridade.calcular(request.tipo(), molde, request.paradaDeProducao());
        molde.enviarParaManutencao();

        OrdemManutencao ordem = new OrdemManutencao(molde, request.tipo(), request.descricaoProblema(),
                request.paradaDeProducao(), prioridade);
        return OrdemResponse.de(ordemRepository.save(ordem));
    }

    @Transactional
    public OrdemResponse concluirOrdem(Long ordemId, ChecklistLiberacao checklist) {
        OrdemManutencao ordem = buscarEntidade(ordemId);
        if (!ordem.estaAberta()) {
            throw new RegraNegocioException("A ordem %d já foi concluída".formatted(ordemId));
        }

        // Se alguma etapa reprovar, a exceção interrompe tudo e nada é alterado.
        cadeiaLiberacao.validar(ordem, checklist);

        ordem.concluir(checklist.causaRaiz());
        ordem.getMolde().liberar(ordem.getTipo() == TipoManutencao.PREVENTIVA);
        return OrdemResponse.de(ordem);
    }

    @Transactional(readOnly = true)
    public List<OrdemResponse> listar(StatusOrdem status) {
        List<OrdemManutencao> ordens = status == null
                ? ordemRepository.findAllBy()
                : ordemRepository.findByStatus(status);
        return ordens.stream()
                .sorted(ORDEM_DA_FILA)
                .map(OrdemResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdemResponse buscar(Long ordemId) {
        return OrdemResponse.de(buscarEntidade(ordemId));
    }

    private OrdemManutencao buscarEntidade(Long ordemId) {
        return ordemRepository.findWithMoldeById(ordemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de manutenção %d não encontrada".formatted(ordemId)));
    }
}
