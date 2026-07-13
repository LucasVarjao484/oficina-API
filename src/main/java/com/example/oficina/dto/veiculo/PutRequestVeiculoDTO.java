package com.example.oficina.dto.veiculo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "EditarVeiculo")
public record PutRequestVeiculoDTO(
        @NotBlank
        @Size(max = 100)
        String marcaVeiculo,
        @NotBlank
        @Size(max = 150)
        String modeloVeiculo,
        @NotNull
        Integer anoVeiculo,
        @NotBlank
        String placaVeiculo
) {
}
