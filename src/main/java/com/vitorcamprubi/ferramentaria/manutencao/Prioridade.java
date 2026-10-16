package com.vitorcamprubi.ferramentaria.manutencao;

/**
 * A ordem de declaração importa: ela é usada para montar a fila da ferramentaria
 * (CRITICA primeiro, BAIXA por último).
 */
public enum Prioridade {
    BAIXA,
    MEDIA,
    ALTA,
    CRITICA
}
