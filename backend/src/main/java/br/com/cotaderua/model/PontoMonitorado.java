package br.com.cotaderua.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pontos_monitorados")
public class PontoMonitorado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 80)
    private String bairro;

    @Column(nullable = false, length = 250)
    private String referencia;

    @Column(nullable = false)
    private boolean simulado;

    protected PontoMonitorado() {
    }

    public PontoMonitorado(
            String nome,
            String bairro,
            String referencia,
            boolean simulado) {
        this.nome = nome;
        this.bairro = bairro;
        this.referencia = referencia;
        this.simulado = simulado;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getBairro() {
        return bairro;
    }

    public String getReferencia() {
        return referencia;
    }

    public boolean isSimulado() {
        return simulado;
    }
}