package com.example.oficina.dto.atendimento;

import com.example.oficina.model.Status;

import java.time.LocalDate;
import java.util.List;

public record ResponseAtendItensDTO(
        LocalDate dataDoAtendimento,
        Integer precoTotalCentavos,
        Status statusAtendimento,
        List<ItemAtendimentoResponseDTO> itensAtend
) {
}
