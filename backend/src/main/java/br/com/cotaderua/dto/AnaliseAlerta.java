package br.com.cotaderua.dto;

public record AnaliseAlerta(
    String classificacao,
    int pontuacao,
    String motivo,
    boolean desatualizado
) {
}