package com.example.oficina.dto.servico;

import com.example.oficina.model.Servico;
import com.example.oficina.model.Veiculo;

import java.util.UUID;

public record ResponseServicoDTO(
        UUID id,
        String nomeServico,
        Integer precoCentavosServico
) {
    public ResponseServicoDTO (Servico servico){
        this(
                servico.getId(),
                servico.getNome(),
                servico.getPrecoCentavos()
        );
    }
}
