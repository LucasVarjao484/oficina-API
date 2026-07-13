package com.example.oficina.dto.dono;

import com.example.oficina.model.Dono;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "Dono")
public record DonoDTO(
        UUID id,
        @NotBlank
        @Size(max = 100)
        String nomeDono,
        @NotBlank
        @CPF
        String cpf,
        @NotBlank
        @Size(max = 150)
        String enderecoDono,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull
        @Past
        LocalDate dataDeNascimentoDono
) {
        public Dono mapearDono(){
                Dono dono = new Dono();
                dono.setNome(this.nomeDono);
                dono.setCpf(this.cpf);
                dono.setEndereco(this.enderecoDono);
                dono.setDataNascimento(this.dataDeNascimentoDono);
                return dono;
        }
}
