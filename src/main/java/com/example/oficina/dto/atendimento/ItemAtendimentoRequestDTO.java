package com.example.oficina.dto.atendimento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

@Schema(name = "CriarItem")
public record ItemAtendimentoRequestDTO(
        @NotNull
        UUID veiculoAtendId,
        @NotNull
        UUID servicoAtendId,
        @NotNull
        Integer quantidade,
        @NotNull
        @PositiveOrZero
        Integer precoFinalCent
) {
}
