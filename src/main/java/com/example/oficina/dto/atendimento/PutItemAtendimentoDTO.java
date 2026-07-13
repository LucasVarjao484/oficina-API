package com.example.oficina.dto.atendimento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "EditarItem")
public record PutItemAtendimentoDTO(
        @NotNull
        Integer quantidade,
        @NotNull
        @PositiveOrZero
        Integer precoFinalCent
) {
}
