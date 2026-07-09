package com.example.oficina.controller;

import com.example.oficina.dto.atendimento.AtendimentosDonoDTO;
import com.example.oficina.dto.atendimento.ResponseAtendDTO;
import com.example.oficina.dto.dono.DonoDTO;
import com.example.oficina.dto.veiculo.ResponseVeiculoDTO;
import com.example.oficina.dto.veiculo.VeiculosDonoDTO;
import com.example.oficina.model.Atendimento;
import com.example.oficina.model.Dono;
import com.example.oficina.model.Status;
import com.example.oficina.model.Veiculo;
import com.example.oficina.service.AtendimentoService;
import com.example.oficina.service.DonoService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/donos")
@RequiredArgsConstructor
@Tag(name = "Donos")
public class DonoController {

    private final DonoService donoService;
    private final VeiculoService veiculoService;
    private final AtendimentoService atendimentoService;

    @Operation(summary = "Buscar por id", description = "Encontra um dono pelo seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dono encontrado"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DonoDTO> encontrarId(@PathVariable("id") String id){
        var idDono = UUID.fromString(id);
        Optional<Dono> donoOptional = donoService.encontrarPorId(idDono);
        if(donoOptional.isPresent()){
            Dono dono = donoOptional.get();
            DonoDTO donoDTO = new DonoDTO(
                    dono.getId(),
                    dono.getNome(),
                    dono.getCpf(),
                    dono.getEndereco(),
                    dono.getDataNascimento());
            return ResponseEntity.ok(donoDTO);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    };

    @Operation(summary = "Buscar donos", description = "Encontra donos de acordo com os parâmetros informados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donos encontrados"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada ! Tipo incorreto de parâmetro")
    }
    )
    @GetMapping
    public ResponseEntity<Page<DonoDTO>> encontrar(@RequestParam(value = "nome-dono", required = false) String nomeDono,
                          @RequestParam(value = "cpf", required = false) String cpf,
                          @RequestParam(value = "endereco-dono", required = false) String enderecoDono,
                          @RequestParam(value = "data-nascimento", required = false) LocalDate dataNascimento,
                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                          @RequestParam(value = "size", defaultValue = "10") Integer size){
        Page<Dono> donos = donoService.encontrar(nomeDono, cpf, enderecoDono, dataNascimento, page, size);
        Page<DonoDTO> resposta = donos.map(dono -> new DonoDTO(
                dono.getId(),
                dono.getNome(),
                dono.getCpf(),
                dono.getEndereco(),
                dono.getDataNascimento()
        ));
        return ResponseEntity.ok(resposta);
    };

    @Operation(summary = "Criar dono", description = "Registra um novo dono")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dono cadastrado"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada !"),
            @ApiResponse(responseCode = "409", description = "Dono já registrado !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos")
    }
    )
    @PostMapping
    public ResponseEntity<Object> criarDono(@RequestBody @Valid DonoDTO donoDTO){
        Dono dono = donoDTO.mapearDono();
        donoService.criarDono(dono);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dono.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    };

    @Operation(summary = "Editar dono", description = "Edita um dono já existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dono editado"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado !"),
            @ApiResponse(responseCode = "409", description = "Dono já desativado !"),
            @ApiResponse(responseCode = "422", description = "Erro de validação ! Verificar Campos incorretos"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Object> editarDono(@PathVariable("id") String id, @RequestBody @Valid DonoDTO donoDTO){
        var uuid = UUID.fromString(id);
        Optional<Dono> donoOptional = donoService.encontrarPorId(uuid);
        if(donoOptional.isPresent()){
            Dono dono = donoOptional.get();
            dono.setNome(donoDTO.nomeDono());
            dono.setCpf(donoDTO.cpf());
            dono.setEndereco(donoDTO.enderecoDono());
            dono.setDataNascimento(donoDTO.dataDeNascimentoDono());
            donoService.editarDono(dono);
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    };

    @Operation(summary = "Desativar dono", description = "Desativa um dono")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dono desativado"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado !"),
            @ApiResponse(responseCode = "400", description = "UUID inválido !")
    }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> desativarDono(@PathVariable("id") String id){
        var uuid = UUID.fromString(id);
        Optional<Dono> donoOptional = donoService.encontrarPorId(uuid);
        if(donoOptional.isPresent()){
            Dono dono = donoOptional.get();
            donoService.desativarDono(dono);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    };

    @Operation(summary = "Buscar veículos de um dono", description = "Busca os veículos de um dono, com opção de buscar veículos com parâmetros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículos encontrados"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado !"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada ! Tipo incorreto de parâmetro")
    }
    )
    @GetMapping("/{id}/veiculos")
    public ResponseEntity<Object> veiculosDono(@PathVariable("id") String idDono,
                             @RequestParam(value = "marca", required = false) String marcaVeiculo,
                             @RequestParam(value = "modelo", required = false) String modeloVeiculo,
                             @RequestParam(value = "ano", required = false) Integer anoVeiculo){
        var id = UUID.fromString(idDono);
        Optional<Dono> donoOptional = donoService.encontrarPorId(id);
        if(donoOptional.isPresent()){
            Dono dono = donoOptional.get();
            DonoDTO donoDTO = new DonoDTO(dono.getId(), dono.getNome(), dono.getCpf(), dono.getEndereco(), dono.getDataNascimento());
            List<Veiculo> lista = veiculoService.veiculosPorDono(id, marcaVeiculo, modeloVeiculo, anoVeiculo);
            List<ResponseVeiculoDTO> listaDTO = lista.stream().map(ResponseVeiculoDTO::new).toList();
            VeiculosDonoDTO resposta = new VeiculosDonoDTO(donoDTO.nomeDono(), donoDTO.cpf(), listaDTO);
            return ResponseEntity.ok(resposta);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    };

    @Operation(summary = "Buscar atendimentos de um dono", description = "Busca os atendimentos de um dono, com opção de buscar atendimentos com parâmetros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atendimentos encontrados"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado !"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada ! Tipo incorreto de parâmetro")
    }
    )
    @GetMapping("/{id}/atendimentos")
    public ResponseEntity<Object> atendimentosDono(@PathVariable("id") String idDono,
                                 @RequestParam(value = "data-atendimento", required = false) LocalDate dataAtendimento,
                                 @RequestParam(value = "total", required = false) Integer totalCentavos,
                                 @RequestParam(value = "status", required = false) String status){
        var id = UUID.fromString(idDono);
        Optional<Dono> donoOptional = donoService.encontrarPorId(id);
        if(donoOptional.isPresent()){
            Dono dono = donoOptional.get();
            DonoDTO donoDTO = new DonoDTO(dono.getId(), dono.getNome(), dono.getCpf(), dono.getEndereco(), dono.getDataNascimento());
            List<Atendimento> lista = atendimentoService.atendimentoPorDono(id, dataAtendimento, totalCentavos, status);
            List<ResponseAtendDTO> listaDTO = lista.stream().map(ResponseAtendDTO::new).toList();
            AtendimentosDonoDTO resposta = new AtendimentosDonoDTO(dono.getNome(), dono.getCpf(), listaDTO);
            return ResponseEntity.ok(resposta);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    };
}
