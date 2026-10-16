package com.vitorcamprubi.ferramentaria.manutencao.prioridade;

import org.springframework.stereotype.Component;

import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

/**
 * Melhoria é planejada com antecedência, então entra no fim da fila e é encaixada
 * quando a ferramentaria tem janela.
 */
@Component
public class PrioridadeMelhoria implements CalculoPrioridade {

    @Override
    public TipoManutencao tipo() {
        return TipoManutencao.MELHORIA;
    }

    @Override
    public Prioridade calcular(Molde molde, boolean paradaDeProducao) {
        return Prioridade.BAIXA;
    }
}
