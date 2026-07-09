package com.example.oficina.dto.atendimento;

import com.example.oficina.model.Atendimento;
import com.example.oficina.model.Status;

import java.time.LocalDate;
import java.util.UUID;

public record ResponseAtendDTO(
        UUID id,
        LocalDate dataDoAtendimento,
        Integer precoTotalCentavos,
        Status statusAtendimento
) {
    public ResponseAtendDTO(Atendimento atendimento){
        this(
                atendimento.getId(),
                atendimento.getDataAtendimento(),
                atendimento.getTotalCentavos(),
                atendimento.getStatus()
        );
    }
}
