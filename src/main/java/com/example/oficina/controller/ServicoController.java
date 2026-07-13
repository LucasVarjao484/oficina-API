package com.example.oficina.controller;

import com.example.oficina.dto.servico.RequestServicoDTO;
import com.example.oficina.dto.servico.ResponseServicoDTO;
import com.example.oficina.model.Servico;
import com.example.oficina.service.ServicoService;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços")
public class ServicoController {

    private final ServicoService servicoService;

    @Operation(summary = "Buscar por id", description = "Encontra um serviço pelo seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> encontrarPorId(@PathVariable("id") String id){
        var uuid = UUID.fromString(id);
        Optional<Servico> servicoOptional = servicoService.encontrarPorId(uuid);
        if(servicoOptional.isPresent()){
            Servico servico = servicoOptional.get();
            ResponseServicoDTO responseServicoDTO = new ResponseServicoDTO(servico);
            return ResponseEntity.ok(responseServicoDTO);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar serviços", description = "Encontra serviços de acordo com os parâmetros informados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "serviços encontrados"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada ! Tipo incorreto de parâmetro")
    }
    )
    @GetMapping
    public ResponseEntity<Object> encontrarServico(
            @RequestParam(value = "nome", required = false) String nomeServico,
            @RequestParam(value = "preco-centavos", required = false) Integer precoCentavos,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size){
        Page<Servico> lista = servicoService.encontrar(nomeServico, precoCentavos, page, size);
        Page<ResponseServicoDTO> resposta = lista.map(servico -> new ResponseServicoDTO(
                servico.getId(),
                servico.getNome(),
                servico.getPrecoCentavos()
        ));
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Criar serviço", description = "Registra um novo serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Serviço cadastrado"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos")
    }
    )
    @PostMapping
    public ResponseEntity<Object> criarServico(@RequestBody @Valid RequestServicoDTO requestServicoDTO){
        Servico servico = new Servico();
        servico.setNome(requestServicoDTO.nomeServico());
        servico.setPrecoCentavos(requestServicoDTO.precoCentavosServico());
        servicoService.criarServico(servico);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(servico.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Editar serviço", description = "Edita um serviço já existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Serviço editado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado !"),
            @ApiResponse(responseCode = "409", description = "Serviço já desativado !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> editarServico(@PathVariable("id") String id, @RequestBody @Valid RequestServicoDTO requestServicoDTO){
        var uuid = UUID.fromString(id);
        Optional<Servico> servicoOptional = servicoService.encontrarPorId(uuid);
        if(servicoOptional.isPresent()){
            Servico servico = servicoOptional.get();
            servico.setNome(requestServicoDTO.nomeServico());
            servico.setPrecoCentavos(requestServicoDTO.precoCentavosServico());
            servicoService.editarServico(servico);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Desativar serviço", description = "Desativa um serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Serviço desativado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado !"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> desativarServico(@PathVariable("id") String id){
        var uuid = UUID.fromString(id);
        Optional<Servico> servicoOptional = servicoService.encontrarPorId(uuid);
        if(servicoOptional.isPresent()){
            Servico servico = servicoOptional.get();
            servicoService.desativarServico(servico);
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
