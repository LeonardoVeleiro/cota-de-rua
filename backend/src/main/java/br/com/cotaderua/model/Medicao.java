package br.com.cotaderua.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicoes")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ponto_id", nullable = false)
    private PontoMonitorado ponto;

    @Column(nullable = false)
    private double nivelAguaCm;

    @Column(nullable = false)
    private OffsetDateTime horario;

    @Column(nullable = false, length = 120)
    private String origem;

    @Column(nullable = false)
    private int confianca;

    @Column(nullable = false)
    private boolean simulado;

    protected Medicao() {
    }

    public Medicao(
            PontoMonitorado ponto,
            double nivelAguaCm,
            OffsetDateTime horario,
            String origem,
            int confianca,
            boolean simulado) {
        this.ponto = ponto;
        this.nivelAguaCm = nivelAguaCm;
        this.horario = horario;
        this.origem = origem;
        this.confianca = confianca;
        this.simulado = simulado;
    }

    public Long getId() {
        return id;
    }

    public PontoMonitorado getPonto() {
        return ponto;
    }

    public double getNivelAguaCm() {
        return nivelAguaCm;
    }

    public OffsetDateTime getHorario() {
        return horario;
    }

    public String getOrigem() {
        return origem;
    }

    public int getConfianca() {
        return confianca;
    }

    public boolean isSimulado() {
        return simulado;
    }
}