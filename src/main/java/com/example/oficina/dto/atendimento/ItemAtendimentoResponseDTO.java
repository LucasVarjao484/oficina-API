package com.example.oficina.dto.atendimento;

import com.example.oficina.dto.servico.ResponseServicoDTO;
import com.example.oficina.dto.veiculo.ResponseVeiculoDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ItemAtendimentoResponseDTO(
        UUID itemAtendId,
        ResponseVeiculoDTO veiculoDTO,
        ResponseServicoDTO servicoDTO,
        Integer quantidade,
        Integer precoFinalCent
) {
}
