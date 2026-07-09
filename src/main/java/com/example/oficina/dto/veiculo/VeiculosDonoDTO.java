package com.example.oficina.dto.veiculo;

import java.util.List;

public record VeiculosDonoDTO(
        String nomeDono,
        String cpf,
        List<ResponseVeiculoDTO> veiculos
) {
}
