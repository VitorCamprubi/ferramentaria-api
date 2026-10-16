package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * O que o ferramenteiro informa ao encerrar a ordem, antes do molde voltar para a injetora.
 * Os campos booleanos são obrigatórios para obrigar uma resposta explícita (sim ou não),
 * em vez de assumir "false" quando o campo não vier no JSON.
 */
public record ChecklistLiberacao(
        @NotNull Boolean refrigeracaoTestada,
        @NotNull Boolean extracaoTestada,
        @NotNull Boolean tryOutAprovado,
        @Size(max = 500) String causaRaiz) {
}
