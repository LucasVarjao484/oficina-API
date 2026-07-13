package com.example.oficina.controller;

import com.example.oficina.dto.atendimento.*;
import com.example.oficina.model.Atendimento;
import com.example.oficina.model.ItemAtendimento;
import com.example.oficina.service.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/atendimentos")
@RequiredArgsConstructor
@Tag(name = "Atendimentos")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    @Operation(summary = "Criar atendimento", description = "Registra um novo atendimento e adiciona um item nele")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Atendimento cadastrado"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada !"),
            @ApiResponse(responseCode = "409", description = "Veículo ou serviço desativado !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos"),
            @ApiResponse(responseCode = "404", description = "Veículo ou serviço não encontrado !")
    }
    )
    @PostMapping
    public ResponseEntity<Object> iniciarAtendimento(@RequestBody @Valid ItemAtendimentoRequestDTO itemAtendimentoRequestDTO){
        Atendimento atendimento = atendimentoService.iniciarAtendimento(itemAtendimentoRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(atendimento.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Adicionar item ao atendimento", description = "Adiciona um novo item a um atendimento já existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado ao atendimento"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos"),
            @ApiResponse(responseCode = "404", description = "Atendimento, veículo ou serviço não encontrado !"),
            @ApiResponse(responseCode = "409", description = "Atendimento encerrado, veículo/serviço desativado, serviço já registrado para o veículo ou veículo de outro proprietário")
    }
    )
    @PostMapping("/{id}/itens")
    public ResponseEntity<Object> adicionarItem(@PathVariable("id") String id, @RequestBody @Valid ItemAtendimentoRequestDTO itemAtendimentoRequestDTO){
        var uuid = UUID.fromString(id);
        Optional<Atendimento> atendimentoOptional = atendimentoService.encontrarPorId(uuid);
        if(atendimentoOptional.isPresent()){
            ItemAtendimento itemAtendimento = atendimentoService.adicionarItem(itemAtendimentoRequestDTO, uuid);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(itemAtendimento.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Encerrar atendimento", description = "Altera o status de um atendimento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Atendimento encerrado"),
            @ApiResponse(responseCode = "409", description = "Atendimento já está encerrado !"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado !"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Object> encerrarAtendimento(@PathVariable("id") String id, @RequestBody @Valid PatchAtendimentoDTO dto){
        var uuid = UUID.fromString(id);
        Optional<Atendimento> atendimentoOptional = atendimentoService.encontrarPorId(uuid);
        if(atendimentoOptional.isPresent()){
            atendimentoService.encerrarAtendimento(uuid, dto.status());
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar itens do atendimento por id", description = "Deleta um item pelo seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item apagado"),
            @ApiResponse(responseCode = "404", description = "Item ou atendimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !"),
            @ApiResponse(responseCode = "409", description = "O atendimento não está mais em aberto, ou o item informado não pertence ao atendimento informado!")
    }
    )
    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Object> deletarItem(@PathVariable("id") String id, @PathVariable("itemId") String itemId){
        var uuid = UUID.fromString(id);
        Optional<Atendimento> atendimentoOptional = atendimentoService.encontrarPorId(uuid);
        if(atendimentoOptional.isPresent()){
            var uuidItem = UUID.fromString(itemId);
            Optional<ItemAtendimento> itemAtendimentoOptional = atendimentoService.encontrarItemPorId(uuidItem);
            if(itemAtendimentoOptional.isPresent()){
                atendimentoService.excluirItem(uuid, uuidItem);
                return ResponseEntity.noContent().build();
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Editar itens do atendimento por id", description = "Altera quantidade e/ou preço de um item, pelo seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item editado"),
            @ApiResponse(responseCode = "404", description = "Item ou atendimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !"),
            @ApiResponse(responseCode = "409", description = "O atendimento não está mais em aberto, ou o item informado não pertence ao atendimento informado!")
    }
    )
    @PutMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Object> atualizarItem(@PathVariable("id") String id,
                                                @PathVariable("itemId") String itemId,
                                                @RequestBody @Valid PutItemAtendimentoDTO putItemAtendimentoDTO){
        var uuid = UUID.fromString(id);
        Optional<Atendimento> atendimentoOptional = atendimentoService.encontrarPorId(uuid);
        if(atendimentoOptional.isPresent()){
            var uuidItem = UUID.fromString(itemId);
            Optional<ItemAtendimento> itemAtendimentoOptional = atendimentoService.encontrarItemPorId(uuidItem);
            if(itemAtendimentoOptional.isPresent()){
                atendimentoService.editarItem(uuid, uuidItem, putItemAtendimentoDTO);
                return ResponseEntity.noContent().build();
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar atendimento por id", description = "Encontra um atendimento pelo seu id, e retorna seus itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Object> encontrarPorId(@PathVariable("id") String id){
        var uuid = UUID.fromString(id);
        Optional<Atendimento> atendimentoOptional = atendimentoService.encontrarPorId(uuid);
        if(atendimentoOptional.isPresent()){
            Atendimento atendimento = atendimentoOptional.get();
            List<ItemAtendimentoResponseDTO> lista = atendimentoService.encontrarItens(uuid);
            ResponseAtendItensDTO resposta = new ResponseAtendItensDTO(
                    atendimento.getDataAtendimento(),
                    atendimento.getTotalCentavos(),
                    atendimento.getStatus(),
                    lista
            );
            return ResponseEntity.ok(resposta);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar atendimentos", description = "Encontra atendimentos de acordo com os parâmetros informados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "atendimentos encontrados"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada ! Tipo incorreto de parâmetro")
    }
    )
    @GetMapping
    public ResponseEntity<Object> encontrar(@RequestParam(value = "data-atendimento", required = false) LocalDate dataAtendimento,
                          @RequestParam(value = "total", required = false) Integer totalCentavos,
                          @RequestParam(value = "status", required = false) String status,
                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                          @RequestParam(value = "size", defaultValue = "10") Integer size){
        Page<Atendimento> atendimentos = atendimentoService.encontrarAtendimento(dataAtendimento, totalCentavos, status, page, size);
        Page<ResponseAtendDTO> resposta = atendimentos.map(atendimento -> new ResponseAtendDTO(atendimento));
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Buscar itens do atendimento por id", description = "Encontra um item pelo seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item ou atendimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !"),
            @ApiResponse(responseCode = "409", description = "O item informado não pertence ao atendimento informado")
    }
    )
    @GetMapping("/{id}/itens/{idItem}")
    public ResponseEntity<Object> encontrarItemId(@PathVariable("id") String id, @PathVariable("idItem") String idItem){
        var uuid = UUID.fromString(id);
        Optional<Atendimento> atendimentoOptional = atendimentoService.encontrarPorId(uuid);
        if(atendimentoOptional.isPresent()){
            var uuidItem = UUID.fromString(idItem);
            Optional<ItemAtendimento> itemAtendimentoOptional = atendimentoService.encontrarItemPorId(uuidItem);
            if(itemAtendimentoOptional.isPresent()){
                ItemAtendimentoResponseDTO resposta = atendimentoService.encontrarItensId(uuid, uuidItem);
                return ResponseEntity.ok(resposta);
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
