package com.example.oficina.service;

import com.example.oficina.model.Veiculo;
import com.example.oficina.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public List<Veiculo> veiculosPorDono(UUID id,
                                String marcaVeiculo,
                                String modeloVeiculo,
                                Integer anoVeiculo){
        return veiculoRepository.encontrarPorDono(id, marcaVeiculo, modeloVeiculo, anoVeiculo);
    }
}
