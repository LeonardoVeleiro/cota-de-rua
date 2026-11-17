package br.com.cotaderua.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "previsoes")
public class PrevisaoSalva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dataPrevisao;

    @Column(nullable = false)
    private double precipitacaoMm;

    @Column(nullable = false)
    private int probabilidadeChuva;

    @Column(nullable = false)
    private OffsetDateTime consultadoEm;

    @Column(nullable = false)
    private String origem;

    @Column(nullable = false)
    private String localReferencia;

    protected PrevisaoSalva() {
    }

    public PrevisaoSalva(
            String dataPrevisao,
            double precipitacaoMm,
            int probabilidadeChuva,
            OffsetDateTime consultadoEm) {
        this.dataPrevisao = dataPrevisao;
        this.precipitacaoMm = precipitacaoMm;
        this.probabilidadeChuva = probabilidadeChuva;
        this.consultadoEm = consultadoEm;
        this.origem = "OPEN_METEO";
        this.localReferencia = "São Paulo — referência municipal";
    }

    public Long getId() {
        return id;
    }

    public String getDataPrevisao() {
        return dataPrevisao;
    }

    public double getPrecipitacaoMm() {
        return precipitacaoMm;
    }

    public int getProbabilidadeChuva() {
        return probabilidadeChuva;
    }

    public OffsetDateTime getConsultadoEm() {
        return consultadoEm;
    }

    public String getOrigem() {
        return origem;
    }

    public String getLocalReferencia() {
        return localReferencia;
    }
}