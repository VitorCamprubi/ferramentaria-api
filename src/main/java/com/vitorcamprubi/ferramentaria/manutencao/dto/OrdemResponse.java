package com.vitorcamprubi.ferramentaria.manutencao.dto;

import java.time.LocalDateTime;

import com.vitorcamprubi.ferramentaria.manutencao.OrdemManutencao;
import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.StatusOrdem;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;

public record OrdemResponse(
        Long id,
        Long moldeId,
        String codigoMolde,
        TipoManutencao tipo,
        String descricaoProblema,
        boolean paradaDeProducao,
        Prioridade prioridade,
        StatusOrdem status,
        String causaRaiz,
        LocalDateTime abertaEm,
        LocalDateTime concluidaEm) {

    public static OrdemResponse de(OrdemManutencao ordem) {
        return new OrdemResponse(
                ordem.getId(),
                ordem.getMolde().getId(),
                ordem.getMolde().getCodigo(),
                ordem.getTipo(),
                ordem.getDescricaoProblema(),
                ordem.isParadaDeProducao(),
                ordem.getPrioridade(),
                ordem.getStatus(),
                ordem.getCausaRaiz(),
                ordem.getAbertaEm(),
                ordem.getConcluidaEm());
    }
}
