package com.example.oficina.dto.atendimento;

import com.example.oficina.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "AlterarStatus")
public record PatchAtendimentoDTO(
        @NotNull
        Status status
) {
}
