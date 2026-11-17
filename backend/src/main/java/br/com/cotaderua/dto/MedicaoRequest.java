package br.com.cotaderua.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicaoRequest(

    @NotNull(message = "Informe o ponto monitorado.")
    @Positive(message = "O ID do ponto deve ser positivo.")
    Long pontoId,

    @NotNull(message = "Informe o nível da água.")
    @DecimalMin(
        value = "0.0",
        message = "O nível da água não pode ser negativo."
    )
    @DecimalMax(
        value = "500.0",
        message = "O limite do protótipo é de 500 cm."
    )
    Double nivelAguaCm

) {
}