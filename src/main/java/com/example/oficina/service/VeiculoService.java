package com.example.oficina.service;

import com.example.oficina.dto.veiculo.RequestVeiculoDTO;
import com.example.oficina.exception.CadastroDuplicadoException;
import com.example.oficina.exception.RecursoNaoEncontradoException;
import com.example.oficina.exception.RecursoDesativadoException;
import com.example.oficina.model.Dono;
import com.example.oficina.model.Veiculo;
import com.example.oficina.repository.DonoRepository;
import com.example.oficina.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final DonoRepository donoRepository;

    public Optional<Veiculo> encontrarPorId(UUID uuid){
        return veiculoRepository.findById(uuid);
    }

    public Page<Veiculo> encontrar(String marcaVeiculo,
                                   String modeloVeiculo,
                                   Integer anoVeiculo,
                                   String placaVeiculo,
                                   Integer page,
                                   Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return veiculoRepository.encontrar(marcaVeiculo, modeloVeiculo, anoVeiculo, placaVeiculo, pageable);
    }

    public Veiculo criarVeiculo(RequestVeiculoDTO requestVeiculoDTO){
        validarVeiculo(requestVeiculoDTO);
        Veiculo veiculo = requestVeiculoDTO.mapearVeiculo();
        veiculo.setDono(donoRepository.findById(requestVeiculoDTO.dono_id()).get());
        veiculo.setAtivo(Boolean.TRUE);
        veiculoRepository.save(veiculo);
        return veiculo;
    }

    public void editarVeiculo(Veiculo veiculo){
        validarEdicao(veiculo);
        veiculo.setPlaca(veiculo.getPlaca().toUpperCase());
        veiculoRepository.save(veiculo);
    }

    public void desativarVeiculo(Veiculo veiculo){
        veiculo.setAtivo(Boolean.FALSE);
        veiculoRepository.save(veiculo);
    }

    public List<Veiculo> veiculosPorDono(UUID id,
                                String marcaVeiculo,
                                String modeloVeiculo,
                                Integer anoVeiculo,
                                String placaVeiculo){
        return veiculoRepository.encontrarPorDono(id, marcaVeiculo, modeloVeiculo, anoVeiculo, placaVeiculo);
    }

    public void validarVeiculo(RequestVeiculoDTO requestVeiculoDTO){
        Optional<Dono> donoOptional = donoRepository.findById(requestVeiculoDTO.dono_id());
        if(donoOptional.isEmpty()){
            throw new RecursoNaoEncontradoException("Dono não encontrado !");
        }
        Optional<Veiculo> veiculoOptional = veiculoRepository.findByPlaca(requestVeiculoDTO.placaVeiculo().toUpperCase());
        if(veiculoOptional.isPresent()){
            throw new CadastroDuplicadoException("Placa de veículo já cadastrada !");
        }
    }

    public void validarEdicao(Veiculo veiculo){
        if(!veiculo.getAtivo()){
            throw new RecursoDesativadoException("Veiculo desativado !");
        }
        Optional<Veiculo> veiculoOptional = veiculoRepository.findByPlaca(veiculo.getPlaca().toUpperCase());
        if(veiculoOptional.isPresent()){
            Veiculo veiculo2 = veiculoOptional.get();
            if(!veiculo2.getId().equals(veiculo.getId())){
                throw new CadastroDuplicadoException("Placa de veículo já cadastrada !");
            }
        }
    }
}
