package com.example.oficina.service;

import com.example.oficina.exception.CadastroDuplicadoException;
import com.example.oficina.exception.RecursoDesativadoException;
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
        validarEdicao(dono);
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

    public void validarEdicao(Dono dono){
        if(!dono.getAtivo()){
            throw new RecursoDesativadoException("Dono desativado !");
        }
        Optional<Dono> donoOptional = donoRepository.findByCpf(dono.getCpf());
        if(donoOptional.isPresent()){
            Dono dono2 = donoOptional.get();
            if(!dono2.getId().equals(dono.getId())){
                throw new CadastroDuplicadoException("CPF já registrado !");
            }
        }
    }
}
