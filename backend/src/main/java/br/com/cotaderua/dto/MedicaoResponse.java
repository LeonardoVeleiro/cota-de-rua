package br.com.cotaderua.dto;

import java.time.OffsetDateTime;

import br.com.cotaderua.model.Medicao;

public record MedicaoResponse(
    Long id,
    Long pontoId,
    String nomePonto,
    double nivelAguaCm,
    OffsetDateTime horario,
    String origem,
    int confianca,
    boolean simulado
) {

    public static MedicaoResponse de(Medicao medicao) {
        return new MedicaoResponse(
            medicao.getId(),
            medicao.getPonto().getId(),
            medicao.getPonto().getNome(),
            medicao.getNivelAguaCm(),
            medicao.getHorario(),
            medicao.getOrigem(),
            medicao.getConfianca(),
            medicao.isSimulado()
        );
    }
}