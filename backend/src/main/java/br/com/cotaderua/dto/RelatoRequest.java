package br.com.cotaderua.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RelatoRequest(

    @NotNull(message = "Informe o ponto monitorado.")
    @Positive(message = "O ID do ponto deve ser positivo.")
    Long pontoId,

    @NotBlank(message = "Descreva a ocorrência.")
    @Size(
        min = 10,
        max = 500,
        message = "A descrição deve ter entre 10 e 500 caracteres."
    )
    String descricao

) {
}