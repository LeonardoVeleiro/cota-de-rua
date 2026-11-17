package br.com.cotaderua.dto;

import java.time.OffsetDateTime;

public record AlertaResponse(
    Long pontoId,
    String nomePonto,
    String bairro,
    Long medicaoId,
    Double nivelAguaCm,
    OffsetDateTime horario,
    String origem,
    Integer confianca,
    String classificacao,
    int pontuacao,
    String motivo,
    boolean desatualizado,
    boolean simulado
) {
}