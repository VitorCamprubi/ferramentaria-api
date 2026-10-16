package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;

/** Última barreira: as peças do try-out precisam ser aprovadas pela qualidade. */
public class VerificacaoTryOut extends EtapaLiberacao {

    @Override
    protected void verificar(OrdemManutencao ordem, ChecklistLiberacao checklist) {
        if (!checklist.tryOutAprovado()) {
            reprovar("As amostras do try-out precisam ser aprovadas pela qualidade");
        }
    }

    @Override
    protected String nome() {
        return "Try-out";
    }
}
