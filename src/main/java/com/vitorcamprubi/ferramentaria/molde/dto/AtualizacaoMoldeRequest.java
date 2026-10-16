package com.vitorcamprubi.ferramentaria.molde.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * O código do molde fica de fora de propósito: ele é a identificação gravada
 * na placa do molde e não muda depois do cadastro.
 */
public record AtualizacaoMoldeRequest(
        @NotBlank @Size(max = 120) String descricao,
        @Positive int cavidades,
        @Positive long limiteCiclosPreventiva) {
}
