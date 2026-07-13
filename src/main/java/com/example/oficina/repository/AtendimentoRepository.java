package com.example.oficina.repository;

import com.example.oficina.model.Atendimento;
import com.example.oficina.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AtendimentoRepository extends JpaRepository<Atendimento, UUID> {

    @Query(value = """
SELECT DISTINCT a.* FROM atendimento a 
JOIN item_atendimento i ON i.atendimento_id = a.id
JOIN veiculo v ON i.veiculo_id = v.id
JOIN dono d ON v.dono_id = d.id
WHERE v.dono_id = (:id)
AND d.ativo = true
AND ((:data)::date IS NULL OR a.data_atendimento = (:data)::date)
AND (:total IS NULL OR a.total_centavos = :total)
AND (:status IS NULL OR a.status = :status)
""", nativeQuery = true)
    List<Atendimento> encontrarPorDono(@Param("id") UUID id,
                                       @Param("data") LocalDate dataAtendimento,
                                       @Param("total") Integer totalCentavos,
                                       @Param("status") String status);

    @Query(value = """
SELECT a.* FROM atendimento a 
WHERE ((:data)::date IS NULL OR a.data_atendimento = (:data)::date)
AND (:total IS NULL OR a.total_centavos = :total)
AND (:status IS NULL OR a.status = :status)
""", nativeQuery = true)
    Page<Atendimento> encontrarAtendimento(@Param("data") LocalDate dataAtendimento,
                                           @Param("total") Integer totalCentavos,
                                           @Param("status") String status,
                                           Pageable pageable);
}
