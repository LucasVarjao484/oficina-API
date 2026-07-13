package com.example.oficina.service;

import com.example.oficina.dto.atendimento.ItemAtendimentoRequestDTO;
import com.example.oficina.dto.atendimento.ItemAtendimentoResponseDTO;
import com.example.oficina.dto.atendimento.PutItemAtendimentoDTO;
import com.example.oficina.dto.servico.ResponseServicoDTO;
import com.example.oficina.dto.veiculo.ResponseVeiculoDTO;
import com.example.oficina.exception.AcaoNaoPermitidaException;
import com.example.oficina.exception.RecursoDesativadoException;
import com.example.oficina.exception.RecursoNaoEncontradoException;
import com.example.oficina.model.*;
import com.example.oficina.repository.AtendimentoRepository;
import com.example.oficina.repository.ItemAtendimentoRepository;
import com.example.oficina.repository.ServicoRepository;
import com.example.oficina.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final ItemAtendimentoRepository itemAtendimentoRepository;

    public List<Atendimento> atendimentoPorDono(UUID id,
                                                LocalDate dataAtendimento,
                                                Integer totalCentavos,
                                                String status){
        return atendimentoRepository.encontrarPorDono(id, dataAtendimento, totalCentavos, status);
    }

    public Page<Atendimento> encontrarAtendimento(LocalDate dataAtendimento,
                                                  Integer totalCentavos,
                                                  String status,
                                                  Integer page,
                                                  Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return atendimentoRepository.encontrarAtendimento(
                dataAtendimento,
                totalCentavos,
                status,
                pageable
        );
    }

    public List<ItemAtendimentoResponseDTO> encontrarItens(UUID atendimentoId){
        List<ItemAtendimento> listaItens = itemAtendimentoRepository.findAllByAtendimentoId(atendimentoId);
        List<ItemAtendimentoResponseDTO> resposta = new ArrayList<>();
        for(ItemAtendimento item : listaItens){

            Veiculo veiculo = item.getVeiculo();
            ResponseVeiculoDTO responseVeiculoDTO = new ResponseVeiculoDTO(veiculo);

            Servico servico = item.getServico();
            ResponseServicoDTO responseServicoDTO = new ResponseServicoDTO(servico);

            ItemAtendimentoResponseDTO itemDTO = new ItemAtendimentoResponseDTO(
                    item.getId(),
                    responseVeiculoDTO,
                    responseServicoDTO,
                    item.getQuantidade(),
                    item.getPrecoFinalCentavos()
            );

            resposta.add(itemDTO);
        }
        return resposta;
    }

    public ItemAtendimentoResponseDTO encontrarItensId(UUID idAtendimento, UUID idItem){
        validarItemAtend(idAtendimento, idItem);
        ItemAtendimento item = encontrarItemPorId(idItem).get();

        Veiculo veiculo = item.getVeiculo();
        ResponseVeiculoDTO responseVeiculoDTO = new ResponseVeiculoDTO(veiculo);

        Servico servico = item.getServico();
        ResponseServicoDTO responseServicoDTO = new ResponseServicoDTO(servico);

        ItemAtendimentoResponseDTO itemDTO = new ItemAtendimentoResponseDTO(
                item.getId(),
                responseVeiculoDTO,
                responseServicoDTO,
                item.getQuantidade(),
                item.getPrecoFinalCentavos()
        );
        return itemDTO;
    }

    @Transactional
    public Atendimento iniciarAtendimento(ItemAtendimentoRequestDTO item){
        validarExistencia(item);

        Integer totalCent = ((item.precoFinalCent()) * (item.quantidade()));
        Atendimento atendimento = new Atendimento();
        atendimento.setDataAtendimento(LocalDate.now());
        atendimento.setTotalCentavos(totalCent);
        atendimento.setStatus(Status.ABERTO);
        atendimentoRepository.save(atendimento);

        ItemAtendimento itemAtendimento = new ItemAtendimento();
        itemAtendimento.setAtendimento(atendimento);
        itemAtendimento.setVeiculo(veiculoRepository.findById(item.veiculoAtendId()).get());
        itemAtendimento.setServico(servicoRepository.findById(item.servicoAtendId()).get());
        itemAtendimento.setPrecoFinalCentavos(item.precoFinalCent());
        itemAtendimento.setQuantidade(item.quantidade());
        itemAtendimentoRepository.save(itemAtendimento);
        return atendimento;
    }

    public Optional<Atendimento> encontrarPorId(UUID uuid){
        return atendimentoRepository.findById(uuid);
    }

    public Optional<ItemAtendimento> encontrarItemPorId(UUID uuid){
        return itemAtendimentoRepository.findById(uuid);
    }

    @Transactional
    public ItemAtendimento adicionarItem(ItemAtendimentoRequestDTO item, UUID atendimentoId){
        validarAtendimento(atendimentoId);
        validarExistencia(item);
        validarVeiculoEServicoNoAtend(item, atendimentoId);
        validarDonoVeiculo(item.veiculoAtendId(), atendimentoId);

        Optional<Atendimento> atendimentoOptional = atendimentoRepository.findById(atendimentoId);
        Atendimento atendimento = atendimentoOptional.get();

        ItemAtendimento itemAtendimento = new ItemAtendimento();
        itemAtendimento.setAtendimento(atendimento);
        itemAtendimento.setVeiculo(veiculoRepository.findById(item.veiculoAtendId()).get());
        itemAtendimento.setServico(servicoRepository.findById(item.servicoAtendId()).get());
        itemAtendimento.setPrecoFinalCentavos(item.precoFinalCent());
        itemAtendimento.setQuantidade(item.quantidade());
        itemAtendimentoRepository.save(itemAtendimento);

        Integer totalCent = ((item.precoFinalCent()) * (item.quantidade()));
        atendimento.setTotalCentavos((atendimento.getTotalCentavos()) + (totalCent));

        atendimentoRepository.save(atendimento);
        return itemAtendimento;
    }

    public void encerrarAtendimento(UUID uuid, Status status){
        validarAtendimento(uuid);
        Optional<Atendimento> atendimentoOptional = atendimentoRepository.findById(uuid);
        Atendimento atendimento = atendimentoOptional.get();
        atendimento.setStatus(status);
        atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void excluirItem(UUID idAtendimento, UUID idItem){
        validarAtendimento(idAtendimento);
        validarItemAtend(idAtendimento, idItem);

        ItemAtendimento itemAtendimento = itemAtendimentoRepository.findById(idItem).get();
        Integer totalRemovido = ((itemAtendimento.getPrecoFinalCentavos()) * (itemAtendimento.getQuantidade()));
        Atendimento atendimento = atendimentoRepository.findById(idAtendimento).get();
        atendimento.setTotalCentavos((atendimento.getTotalCentavos()) - (totalRemovido));

        itemAtendimentoRepository.deleteById(idItem);
    }

    @Transactional
    public void editarItem(UUID idAtendimento, UUID idItem, PutItemAtendimentoDTO putItemAtendimentoDTO){
        validarAtendimento(idAtendimento);
        validarItemAtend(idAtendimento, idItem);

        ItemAtendimento itemAtendimento = itemAtendimentoRepository.findById(idItem).get();
        Integer totalRemovido = ((itemAtendimento.getPrecoFinalCentavos()) * (itemAtendimento.getQuantidade()));
        Atendimento atendimento = atendimentoRepository.findById(idAtendimento).get();
        atendimento.setTotalCentavos((atendimento.getTotalCentavos()) - (totalRemovido));

        itemAtendimento.setQuantidade(putItemAtendimentoDTO.quantidade());
        itemAtendimento.setPrecoFinalCentavos(putItemAtendimentoDTO.precoFinalCent());
        Integer totalAdicionado = ((putItemAtendimentoDTO.precoFinalCent()) * putItemAtendimentoDTO.quantidade());
        atendimento.setTotalCentavos((atendimento.getTotalCentavos()) + (totalAdicionado));

    }

    public void validarExistencia(ItemAtendimentoRequestDTO itemAtendimentoRequestDTO){
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(itemAtendimentoRequestDTO.veiculoAtendId());
        if(veiculoOptional.isEmpty()){
            throw new RecursoNaoEncontradoException("Veiculo não encontrado !");
        }
        if(!veiculoOptional.get().getAtivo()){
            throw new RecursoDesativadoException("Veiculo desativado !");
        }
        Optional<Servico> servicoOptional = servicoRepository.findById(itemAtendimentoRequestDTO.servicoAtendId());
        if(servicoOptional.isEmpty()){
            throw new RecursoNaoEncontradoException("Serviço não encontrado !");
        }
        if(!servicoOptional.get().getAtivo()){
            throw new RecursoDesativadoException("Serviço desativado !");
        }
    }

    public void validarAtendimento(UUID idAtendimento){
        Optional<Atendimento> atendimentoOptional = atendimentoRepository.findById(idAtendimento);
        if(atendimentoOptional.get().getStatus() != Status.ABERTO){
            throw new RecursoDesativadoException("Atendimento não está mais em aberto !");
        }
    }
    public void validarVeiculoEServicoNoAtend(ItemAtendimentoRequestDTO itemAtendimentoRequestDTO, UUID idAtendimento){
        Optional<ItemAtendimento> itemAtendimentoOptional = itemAtendimentoRepository.findByAtendimentoIdAndVeiculoIdAndServicoId(
                idAtendimento,
                itemAtendimentoRequestDTO.veiculoAtendId(),
                itemAtendimentoRequestDTO.servicoAtendId()
        );
        if(itemAtendimentoOptional.isPresent()){
            throw new AcaoNaoPermitidaException("Esse serviço já está registrado para este veículo no atendimento informado !" +
                    " É possível alterar a quantidade e/ou preço do item existente, ou deletá-lo");
        }
    }

    public void validarDonoVeiculo(UUID idVeiculo, UUID idAtendimento){
        Optional<Veiculo> veiculo = veiculoRepository.findById(idVeiculo);
        Optional<Veiculo> veiculoOptional = veiculoRepository.encontrarUmVeiculoPorAtend(idAtendimento);
        if(!veiculo.get().getDono().getId().equals(veiculoOptional.get().getDono().getId())){
            throw new AcaoNaoPermitidaException("Este veículo não pertence ao mesmo dono dos outros veículos deste atendimento");
        }
    }

    public void validarItemAtend(UUID idAtendimento, UUID idItem){
        Optional<ItemAtendimento> itemOptional = itemAtendimentoRepository.findByIdAndAtendimentoId(idItem, idAtendimento);
        if(itemOptional.isEmpty()){
            throw new AcaoNaoPermitidaException("Esse item não pertence ao atendimento informado !");
        }
    }
}
