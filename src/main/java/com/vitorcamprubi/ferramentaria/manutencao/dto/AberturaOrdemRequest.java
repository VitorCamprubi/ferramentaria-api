package com.vitorcamprubi.ferramentaria.manutencao.dto;

import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * {@code paradaDeProducao} é opcional e vale {@code false} quando não é enviado:
 * só faz diferença na corretiva, quando o defeito parou a injetora.
 */
public record AberturaOrdemRequest(
        @NotNull Long moldeId,
        @NotNull TipoManutencao tipo,
        @NotBlank @Size(max = 500) String descricaoProblema,
        boolean paradaDeProducao) {
}
