package com.example.oficina.dto.servico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "CriarServico")
public record RequestServicoDTO(
        @NotBlank
        @Size(max = 200)
        String nomeServico,
        @NotNull
        @PositiveOrZero
        Integer precoCentavosServico
) {
}
