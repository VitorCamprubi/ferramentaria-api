package com.vitorcamprubi.ferramentaria.manutencao;

public enum TipoManutencao {
    /** Revisão programada por número de ciclos: limpeza, lubrificação, troca de itens de desgaste. */
    PREVENTIVA,
    /** Defeito que apareceu na produção: rebarba, peça presa, vazamento de água, pino quebrado. */
    CORRETIVA,
    /** Alteração planejada no molde, como ajuste de produto ou melhoria de refrigeração. */
    MELHORIA
}
