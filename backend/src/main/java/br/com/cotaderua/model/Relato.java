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
@Table(name = "relatos")
public class Relato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ponto_id", nullable = false)
    private PontoMonitorado ponto;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private OffsetDateTime horario;

    @Column(nullable = false, length = 40)
    private String origem;

    @Column(nullable = false)
    private int confianca;

    @Column(nullable = false)
    private boolean simulado;

    protected Relato() {
    }

    public Relato(
            PontoMonitorado ponto,
            String descricao,
            OffsetDateTime horario,
            boolean simulado) {
        this.ponto = ponto;
        this.descricao = descricao;
        this.horario = horario;
        this.origem = "RELATO_COMUNITARIO";

        // Pontuação inicial do protótipo para um relato não verificado.
        this.confianca = 40;
        this.simulado = simulado;
    }

    public Long getId() {
        return id;
    }

    public PontoMonitorado getPonto() {
        return ponto;
    }

    public String getDescricao() {
        return descricao;
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