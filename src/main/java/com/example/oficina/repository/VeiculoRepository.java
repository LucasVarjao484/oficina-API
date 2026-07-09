package com.example.oficina.repository;

import com.example.oficina.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {

    @Query(value = """
SELECT v.* FROM veiculo v JOIN dono d ON d.id = v.dono_id
WHERE d.id = (:id)
AND (:marca IS NULL OR v.marca ILIKE '%' || :marca || '%')
AND (:modelo IS NULL OR v.modelo ILIKE '%' || :modelo || '%')
AND (:ano IS NULL OR v.ano = :ano)
""", nativeQuery = true)
    List<Veiculo> encontrarPorDono(@Param("id") UUID id,
                                    @Param("marca") String marcaVeiculo,
                                    @Param("modelo") String modeloVeiculo,
                                    @Param("ano") Integer anoVeiculo);
}
