package com.example.oficina.dto.veiculo;

import com.example.oficina.model.Veiculo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(name = "CriarVeiculo")
public record RequestVeiculoDTO(
        @NotBlank
        @Size(max = 100)
        String marcaVeiculo,
        @NotBlank
        @Size(max = 150)
        String modeloVeiculo,
        @NotNull
        Integer anoVeiculo,
        @NotBlank
        String placaVeiculo,
        @NotNull
        UUID dono_id
) {
        public Veiculo mapearVeiculo(){
                Veiculo veiculo = new Veiculo();
                veiculo.setMarca(this.marcaVeiculo);
                veiculo.setModelo(this.modeloVeiculo);
                veiculo.setAno(this.anoVeiculo);
                veiculo.setPlaca(this.placaVeiculo.toUpperCase());
                return veiculo;
        }
}
