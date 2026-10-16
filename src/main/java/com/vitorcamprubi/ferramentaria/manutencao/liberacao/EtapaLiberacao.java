package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import com.vitorcamprubi.ferramentaria.common.exception.LiberacaoRecusadaException;
import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;

/**
 * <b>Chain of Responsibility</b>: cada etapa do checklist de liberação é um elo
 * da corrente. A etapa faz a sua verificação e, se estiver tudo certo, passa a
 * ordem adiante para a próxima. Se encontrar um problema, interrompe a corrente
 * e o molde não é liberado.
 * <p>
 * A vantagem em relação a um bloco grande de {@code if}s é que cada verificação
 * fica isolada e testável, e a ordem das etapas é definida num único lugar
 * ({@link LiberacaoConfig}), igual à sequência que a ferramentaria segue no chão de fábrica.
 */
public abstract class EtapaLiberacao {

    private EtapaLiberacao proxima;

    /**
     * Encadeia a próxima etapa e a devolve, permitindo montar a corrente de forma fluente:
     * {@code a.entaoVerificar(b).entaoVerificar(c)}.
     */
    public EtapaLiberacao entaoVerificar(EtapaLiberacao proxima) {
        this.proxima = proxima;
        return proxima;
    }

    /** Executa esta etapa e, se ela aprovar, segue para a próxima da corrente. */
    public final void validar(OrdemManutencao ordem, ChecklistLiberacao checklist) {
        verificar(ordem, checklist);
        if (proxima != null) {
            proxima.validar(ordem, checklist);
        }
    }

    /** Verificação específica da etapa. Deve chamar {@link #reprovar(String)} se encontrar problema. */
    protected abstract void verificar(OrdemManutencao ordem, ChecklistLiberacao checklist);

    /** Nome exibido para o usuário quando esta etapa reprova a liberação. */
    protected abstract String nome();

    protected void reprovar(String motivo) {
        throw new LiberacaoRecusadaException(nome(), motivo);
    }
}
