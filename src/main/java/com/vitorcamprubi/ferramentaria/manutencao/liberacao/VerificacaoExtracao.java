package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;

/** Pino extrator travado ou quebrado prende a peça e pode danificar o molde no primeiro ciclo. */
public class VerificacaoExtracao extends EtapaLiberacao {

    @Override
    protected void verificar(OrdemManutencao ordem, ChecklistLiberacao checklist) {
        if (!checklist.extracaoTestada()) {
            reprovar("O sistema de extração (pinos, placa extratora e retorno) precisa ser testado");
        }
    }

    @Override
    protected String nome() {
        return "Extração";
    }
}
