package com.example.oficina.service;

import com.example.oficina.model.Atendimento;
import com.example.oficina.model.Status;
import com.example.oficina.repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;

    public List<Atendimento> atendimentoPorDono(UUID id,
                                                LocalDate dataAtendimento,
                                                Integer totalCentavos,
                                                String status){
        return atendimentoRepository.encontrarPorDono(id, dataAtendimento, totalCentavos, status);
    }
}
