package com.example.oficina.dto.veiculo;

import com.example.oficina.model.Veiculo;

import java.util.UUID;

public record ResponseVeiculoDTO(
        UUID id,
        String marcaVeiculo,
        String modeloVeiculo,
        Integer anoVeiculo
) {
    public ResponseVeiculoDTO (Veiculo veiculo){
        this(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno()
        );
    }
}
