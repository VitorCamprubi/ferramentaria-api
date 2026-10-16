package com.vitorcamprubi.ferramentaria.molde.dto;

import jakarta.validation.constraints.Positive;

public record RegistroCiclosRequest(@Positive long quantidade) {
}
