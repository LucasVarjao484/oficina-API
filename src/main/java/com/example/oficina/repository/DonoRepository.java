package com.example.oficina.repository;

import com.example.oficina.model.Dono;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DonoRepository extends JpaRepository<Dono, UUID> {
    Optional<Dono> findByCpf(String cpf);


    @Query(value = """
SELECT * FROM dono d WHERE (:nomeDono IS NULL OR d.nome ILIKE '%' || :nomeDono || '%')
  AND (:enderecoDono IS NULL OR d.endereco ILIKE '%' || :enderecoDono || '%')
  AND (:cpf IS NULL OR d.cpf LIKE '%' || :cpf || '%')
  AND ((:dataNascimento)::date IS NULL OR d.data_nascimento = (:dataNascimento)::date)
""", nativeQuery = true)
    Page<Dono> encontrar(@Param(value = "nomeDono") String nomeDono,
                         @Param(value = "cpf") String cpf,
                         @Param(value = "enderecoDono") String enderecoDono,
                         @Param(value = "dataNascimento") LocalDate dataNascimento,
                         Pageable pageable);
}

