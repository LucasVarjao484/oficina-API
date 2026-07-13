package com.example.oficina.controller;

import com.example.oficina.dto.veiculo.PutRequestVeiculoDTO;
import com.example.oficina.dto.veiculo.RequestVeiculoDTO;
import com.example.oficina.dto.veiculo.ResponseVeiculoDTO;
import com.example.oficina.model.Veiculo;
import com.example.oficina.service.VeiculoService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/veiculos")
@Tag(name = "Veículos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @Operation(summary = "Buscar por id", description = "Encontra um veículo pelo seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> encontrarPorId(@PathVariable("id") String id){
        var uuid = UUID.fromString(id);
        Optional<Veiculo> veiculoOptional = veiculoService.encontrarPorId(uuid);
        if(veiculoOptional.isPresent()){
            Veiculo veiculo = veiculoOptional.get();
            ResponseVeiculoDTO responseVeiculoDTO = new ResponseVeiculoDTO(veiculo);
            return ResponseEntity.ok(responseVeiculoDTO);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar veículos", description = "Encontra veículos de acordo com os parâmetros informados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículos encontrados"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada ! Tipo incorreto de parâmetro")
    }
    )
    @GetMapping
    public ResponseEntity<Object> encontrarVeiculo(
            @RequestParam(value = "marca", required = false) String marcaVeiculo,
            @RequestParam(value = "modelo", required = false) String modeloVeiculo,
            @RequestParam(value = "ano", required = false) Integer anoVeiculo,
            @RequestParam(value = "placa", required = false) String placaVeiculo,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size){
        Page<Veiculo> lista = veiculoService.encontrar(marcaVeiculo, modeloVeiculo, anoVeiculo, placaVeiculo, page, size);
        Page<ResponseVeiculoDTO> resposta = lista.map(veiculo -> new ResponseVeiculoDTO(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getPlaca()
        ));
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Criar veículo", description = "Registra um novo veículo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Veículo cadastrado"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada !"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado !"),
            @ApiResponse(responseCode = "409", description = "Veículo já registrado !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos")
    }
    )
    @PostMapping
    public ResponseEntity<Object> cadastrarVeiculo(@RequestBody @Valid RequestVeiculoDTO requestVeiculoDTO){
        Veiculo veiculo = veiculoService.criarVeiculo(requestVeiculoDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(veiculo.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Editar veículo", description = "Edita um veículo já existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Veículo editado"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado !"),
            @ApiResponse(responseCode = "409", description = "Veículo desativado !"),
            @ApiResponse(responseCode = "409", description = "Placa de veículo já registrada !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> editarVeiculo(@PathVariable("id") String id, @RequestBody @Valid PutRequestVeiculoDTO putRequestVeiculoDTO){
        var uuid = UUID.fromString(id);
        Optional<Veiculo> veiculoOptional = veiculoService.encontrarPorId(uuid);
        if(veiculoOptional.isPresent()){
            Veiculo veiculo = veiculoOptional.get();
            veiculo.setMarca(putRequestVeiculoDTO.marcaVeiculo());
            veiculo.setModelo(putRequestVeiculoDTO.modeloVeiculo());
            veiculo.setAno(putRequestVeiculoDTO.anoVeiculo());
            veiculo.setPlaca(putRequestVeiculoDTO.placaVeiculo());
            veiculoService.editarVeiculo(veiculo);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Desativar veículo", description = "Desativa um veículo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Veículo desativado"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado !"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> desativarVeiculo(@PathVariable("id") String id){
        var uuid = UUID.fromString(id);
        Optional<Veiculo> veiculoOptional = veiculoService.encontrarPorId(uuid);
        if(veiculoOptional.isPresent()){
            Veiculo veiculo = veiculoOptional.get();
            veiculoService.desativarVeiculo(veiculo);
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
