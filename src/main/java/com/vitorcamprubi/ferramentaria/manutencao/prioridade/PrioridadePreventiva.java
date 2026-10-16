package com.vitorcamprubi.ferramentaria.manutencao.prioridade;

import org.springframework.stereotype.Component;

import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

/**
 * Na preventiva a urgência depende de quanto o molde já rodou em relação ao limite
 * de ciclos definido para ele. Passou do limite, o risco de quebra aumenta.
 */
@Component
public class PrioridadePreventiva implements CalculoPrioridade {

    static final double LIMITE_ATINGIDO = 100.0;
    static final double PROXIMO_DO_LIMITE = 80.0;

    @Override
    public TipoManutencao tipo() {
        return TipoManutencao.PREVENTIVA;
    }

    @Override
    public Prioridade calcular(Molde molde, boolean paradaDeProducao) {
        double uso = molde.percentualUsoPreventiva();
        if (uso >= LIMITE_ATINGIDO) {
            return Prioridade.ALTA;
        }
        if (uso >= PROXIMO_DO_LIMITE) {
            return Prioridade.MEDIA;
        }
        return Prioridade.BAIXA;
    }
}
