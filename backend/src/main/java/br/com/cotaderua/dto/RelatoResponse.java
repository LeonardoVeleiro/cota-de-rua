package br.com.cotaderua.dto;

import java.time.OffsetDateTime;

import br.com.cotaderua.model.Relato;

public record RelatoResponse(
    Long id,
    Long pontoId,
    String nomePonto,
    String descricao,
    OffsetDateTime horario,
    String origem,
    int confianca,
    boolean simulado
) {

    public static RelatoResponse de(Relato relato) {
        return new RelatoResponse(
            relato.getId(),
            relato.getPonto().getId(),
            relato.getPonto().getNome(),
            relato.getDescricao(),
            relato.getHorario(),
            relato.getOrigem(),
            relato.getConfianca(),
            relato.isSimulado()
        );
    }
}