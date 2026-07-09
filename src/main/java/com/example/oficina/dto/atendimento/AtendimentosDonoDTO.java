package com.example.oficina.dto.atendimento;

import java.util.List;

public record AtendimentosDonoDTO(
        String nomeDono,
        String cpf,
        List<ResponseAtendDTO> atendimentos
) {
}
