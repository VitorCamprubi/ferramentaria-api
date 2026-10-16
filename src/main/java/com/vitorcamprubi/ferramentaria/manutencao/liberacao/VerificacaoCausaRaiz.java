package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;

/**
 * Só se aplica à corretiva: se ninguém registra por que o molde quebrou,
 * o mesmo defeito volta na próxima semana. Nas outras ordens a etapa só passa adiante.
 */
public class VerificacaoCausaRaiz extends EtapaLiberacao {

    @Override
    protected void verificar(OrdemManutencao ordem, ChecklistLiberacao checklist) {
        boolean semCausaRaiz = checklist.causaRaiz() == null || checklist.causaRaiz().isBlank();
        if (ordem.getTipo() == TipoManutencao.CORRETIVA && semCausaRaiz) {
            reprovar("Manutenção corretiva exige o registro da causa raiz do defeito");
        }
    }

    @Override
    protected String nome() {
        return "Causa raiz";
    }
}
