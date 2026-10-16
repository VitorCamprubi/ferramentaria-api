package com.vitorcamprubi.ferramentaria.molde;

import com.vitorcamprubi.ferramentaria.common.exception.RegraNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Molde de injeção acompanhado pela ferramentaria.
 * <p>
 * O contador {@code ciclosDesdePreventiva} sobe conforme a produção injeta peças
 * e volta a zero quando uma manutenção preventiva é concluída. As regras de
 * mudança de status ficam aqui dentro, e não espalhadas pelos services.
 */
@Entity
@Table(name = "molde")
public class Molde {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String descricao;

    @Column(nullable = false)
    private int cavidades;

    @Column(nullable = false)
    private long ciclosDesdePreventiva;

    @Column(nullable = false)
    private long limiteCiclosPreventiva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMolde status;

    protected Molde() {
        // exigido pelo JPA
    }

    public Molde(String codigo, String descricao, int cavidades, long limiteCiclosPreventiva) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.cavidades = cavidades;
        this.limiteCiclosPreventiva = limiteCiclosPreventiva;
        this.ciclosDesdePreventiva = 0;
        this.status = StatusMolde.DISPONIVEL;
    }

    public void atualizarDados(String descricao, int cavidades, long limiteCiclosPreventiva) {
        this.descricao = descricao;
        this.cavidades = cavidades;
        this.limiteCiclosPreventiva = limiteCiclosPreventiva;
    }

    public void registrarCiclos(long quantidade) {
        if (estaEmManutencao()) {
            throw new RegraNegocioException(
                    "O molde %s está em manutenção e não pode registrar ciclos de produção".formatted(codigo));
        }
        this.ciclosDesdePreventiva += quantidade;
    }

    public void enviarParaManutencao() {
        if (estaEmManutencao()) {
            throw new RegraNegocioException(
                    "O molde %s já está em manutenção com uma ordem aberta".formatted(codigo));
        }
        this.status = StatusMolde.EM_MANUTENCAO;
    }

    /**
     * Devolve o molde para a produção. Só a preventiva zera o contador de ciclos:
     * uma corretiva resolve o defeito pontual, mas não substitui a revisão completa.
     */
    public void liberar(boolean preventivaRealizada) {
        this.status = StatusMolde.DISPONIVEL;
        if (preventivaRealizada) {
            this.ciclosDesdePreventiva = 0;
        }
    }

    public double percentualUsoPreventiva() {
        return ciclosDesdePreventiva * 100.0 / limiteCiclosPreventiva;
    }

    public boolean estaEmManutencao() {
        return status == StatusMolde.EM_MANUTENCAO;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCavidades() {
        return cavidades;
    }

    public long getCiclosDesdePreventiva() {
        return ciclosDesdePreventiva;
    }

    public long getLimiteCiclosPreventiva() {
        return limiteCiclosPreventiva;
    }

    public StatusMolde getStatus() {
        return status;
    }
}
