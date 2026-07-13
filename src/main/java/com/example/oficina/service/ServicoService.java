package com.example.oficina.service;

import com.example.oficina.exception.RecursoDesativadoException;
import com.example.oficina.model.Servico;
import com.example.oficina.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public Optional<Servico> encontrarPorId(UUID id){
        return servicoRepository.findById(id);
    }

    public Page<Servico> encontrar(
            String nomeServico,
            Integer precoCentavos,
            Integer page,
            Integer size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return servicoRepository.encontrar(nomeServico, precoCentavos, pageable);
    }

    public void criarServico(Servico servico){
        servico.setAtivo(Boolean.TRUE);
        servicoRepository.save(servico);
    }

    public void editarServico(Servico servico){
        if(!servico.getAtivo()){
            throw new RecursoDesativadoException("Serviço desativado !");
        }
        servicoRepository.save(servico);
    }

    public void desativarServico(Servico servico){
        servico.setAtivo(Boolean.FALSE);
        servicoRepository.save(servico);
    }
}
