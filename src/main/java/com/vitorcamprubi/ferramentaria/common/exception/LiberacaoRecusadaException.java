package com.vitorcamprubi.ferramentaria.common.exception;

/**
 * Lançada por uma etapa do checklist de liberação quando o molde
 * ainda não pode voltar para a produção.
 */
public class LiberacaoRecusadaException extends RuntimeException {

    private final String etapa;

    public LiberacaoRecusadaException(String etapa, String motivo) {
        super(motivo);
        this.etapa = etapa;
    }

    public String getEtapa() {
        return etapa;
    }
}
