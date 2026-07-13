package com.example.oficina.repository;

import com.example.oficina.model.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {

    @Query(value = """
SELECT * FROM servico s 
WHERE (:nome IS NULL OR s.nome ILIKE '%' || :nome || '%')
AND (:preco IS NULL OR s.preco_padrao_centavos = :preco)
""", nativeQuery = true)
    Page<Servico> encontrar(
            @Param("nome") String nomeServico,
            @Param("preco") Integer precoCentavos,
            Pageable pageable
    );

}
