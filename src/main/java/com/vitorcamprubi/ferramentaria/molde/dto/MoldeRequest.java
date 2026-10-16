package com.vitorcamprubi.ferramentaria.molde.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MoldeRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 120) String descricao,
        @Positive int cavidades,
        @Positive long limiteCiclosPreventiva) {
}
