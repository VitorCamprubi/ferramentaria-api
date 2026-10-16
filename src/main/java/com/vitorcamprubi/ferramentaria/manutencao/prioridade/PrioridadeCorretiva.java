package com.vitorcamprubi.ferramentaria.manutencao.prioridade;

import org.springframework.stereotype.Component;

import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

/**
 * Molde que quebrou e parou a injetora é o pior cenário: a linha fica sem produzir.
 * Se o defeito ainda permite rodar (ex.: rebarba leve), continua sendo urgente, mas não crítico.
 */
@Component
public class PrioridadeCorretiva implements CalculoPrioridade {

    @Override
    public TipoManutencao tipo() {
        return TipoManutencao.CORRETIVA;
    }

    @Override
    public Prioridade calcular(Molde molde, boolean paradaDeProducao) {
        return paradaDeProducao ? Prioridade.CRITICA : Prioridade.ALTA;
    }
}
