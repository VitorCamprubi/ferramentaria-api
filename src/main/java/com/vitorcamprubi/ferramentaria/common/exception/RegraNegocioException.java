package com.vitorcamprubi.ferramentaria.common.exception;

/**
 * Violação de uma regra do processo da ferramentaria
 * (ex.: abrir uma segunda ordem para um molde que já está em manutenção).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
