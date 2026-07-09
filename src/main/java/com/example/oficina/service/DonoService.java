package com.example.oficina.service;

import com.example.oficina.exception.CadastroDuplicadoException;
import com.example.oficina.exception.DonoDesativadoException;
import com.example.oficina.model.Dono;
import com.example.oficina.repository.DonoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonoService {

    private final DonoRepository donoRepository;

    public Optional<Dono> encontrarPorId(UUID id){
        return donoRepository.findById(id);
    };

    public Page<Dono> encontrar(String nomeDono,
                          String cpf,
                          String enderecoDono,
                          LocalDate dataNascimento,
                          Integer page,
                          Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return donoRepository.encontrar(nomeDono, cpf, enderecoDono, dataNascimento, pageable);
    };

    public void criarDono(Dono dono){
        validarDono(dono);
        dono.setAtivo(Boolean.TRUE);
        donoRepository.save(dono);
    };

    public void editarDono(Dono dono){
        validarAtivo(dono);
        donoRepository.save(dono);
    };

    public void desativarDono(Dono dono){
        dono.setAtivo(false);
        dono.setDataDelecao(LocalDate.now());
        donoRepository.save(dono);
    };

    public void validarDono(Dono dono){
        Optional<Dono> donoOptional = donoRepository.findByCpf(dono.getCpf());
        if(donoOptional.isPresent()){
            throw new CadastroDuplicadoException("Dono já registrado !");
        }
    }

    public void validarAtivo(Dono dono){
        if(!dono.getAtivo()){
            throw new DonoDesativadoException("Dono desativado !");
        }
    }
}
