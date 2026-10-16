package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;

/** Circuito de água vazando dentro da cavidade estraga peça e pode oxidar o aço do molde. */
public class VerificacaoRefrigeracao extends EtapaLiberacao {

    @Override
    protected void verificar(OrdemManutencao ordem, ChecklistLiberacao checklist) {
        if (!checklist.refrigeracaoTestada()) {
            reprovar("Os circuitos de refrigeração precisam ser testados quanto a vazamento antes da liberação");
        }
    }

    @Override
    protected String nome() {
        return "Refrigeração";
    }
}
