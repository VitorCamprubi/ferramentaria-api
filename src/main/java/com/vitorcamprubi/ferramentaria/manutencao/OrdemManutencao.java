package com.vitorcamprubi.ferramentaria.manutencao;

import java.time.LocalDateTime;

import com.vitorcamprubi.ferramentaria.common.exception.RegraNegocioException;
import com.vitorcamprubi.ferramentaria.molde.Molde;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordem_manutencao")
public class OrdemManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "molde_id", nullable = false)
    private Molde molde;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoManutencao tipo;

    @Column(nullable = false, length = 500)
    private String descricaoProblema;

    @Column(nullable = false)
    private boolean paradaDeProducao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrdem status;

    @Column(length = 500)
    private String causaRaiz;

    @Column(nullable = false)
    private LocalDateTime abertaEm;

    private LocalDateTime concluidaEm;

    protected OrdemManutencao() {
        // exigido pelo JPA
    }

    public OrdemManutencao(Molde molde, TipoManutencao tipo, String descricaoProblema,
                           boolean paradaDeProducao, Prioridade prioridade) {
        this.molde = molde;
        this.tipo = tipo;
        this.descricaoProblema = descricaoProblema;
        this.paradaDeProducao = paradaDeProducao;
        this.prioridade = prioridade;
        this.status = StatusOrdem.ABERTA;
        this.abertaEm = LocalDateTime.now();
    }

    public void concluir(String causaRaiz) {
        if (!estaAberta()) {
            throw new RegraNegocioException("A ordem %d já foi concluída".formatted(id));
        }
        this.status = StatusOrdem.CONCLUIDA;
        this.causaRaiz = causaRaiz;
        this.concluidaEm = LocalDateTime.now();
    }

    public boolean estaAberta() {
        return status == StatusOrdem.ABERTA;
    }

    public Long getId() {
        return id;
    }

    public Molde getMolde() {
        return molde;
    }

    public TipoManutencao getTipo() {
        return tipo;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public boolean isParadaDeProducao() {
        return paradaDeProducao;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public StatusOrdem getStatus() {
        return status;
    }

    public String getCausaRaiz() {
        return causaRaiz;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public LocalDateTime getConcluidaEm() {
        return concluidaEm;
    }
}
