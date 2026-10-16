package com.vitorcamprubi.ferramentaria.manutencao.prioridade;

import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

/**
 * <b>Strategy</b>: cada tipo de manutenção tem a sua própria regra de prioridade.
 * <p>
 * Em vez de um {@code switch} crescendo dentro do service, cada regra vive na sua
 * classe. Para criar um tipo novo de manutenção basta adicionar uma implementação
 * anotada com {@code @Component}: nenhuma classe existente precisa ser alterada.
 */
public interface CalculoPrioridade {

    /** Tipo de manutenção atendido por esta estratégia. */
    TipoManutencao tipo();

    Prioridade calcular(Molde molde, boolean paradaDeProducao);
}
