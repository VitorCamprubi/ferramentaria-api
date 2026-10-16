package com.vitorcamprubi.ferramentaria.molde.dto;

import com.vitorcamprubi.ferramentaria.molde.Molde;
import com.vitorcamprubi.ferramentaria.molde.StatusMolde;

public record MoldeResponse(
        Long id,
        String codigo,
        String descricao,
        int cavidades,
        long ciclosDesdePreventiva,
        long limiteCiclosPreventiva,
        double percentualUsoPreventiva,
        StatusMolde status) {

    public static MoldeResponse de(Molde molde) {
        return new MoldeResponse(
                molde.getId(),
                molde.getCodigo(),
                molde.getDescricao(),
                molde.getCavidades(),
                molde.getCiclosDesdePreventiva(),
                molde.getLimiteCiclosPreventiva(),
                Math.round(molde.percentualUsoPreventiva() * 10) / 10.0,
                molde.getStatus());
    }
}
