package com.example.oficina.repository;

import com.example.oficina.model.ItemAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemAtendimentoRepository extends JpaRepository<ItemAtendimento, UUID> {

//    @Query(value = """
//SELECT i.* FROM item_atendimento i
//WHERE i.atendimento_id = (:atendimentoId)
//AND (:veiculoId) = i.veiculo_id
//AND (:servicoId) = i.servico_id
//""", nativeQuery = true)
//    Optional<ItemAtendimento> teste(@Param("veiculoId") UUID veiculoAtendId,
//                                    @Param("servicoId") UUID servicoAtendId,
//                                    @Param("atendimentoId") UUID AtendimentoId);

    Optional<ItemAtendimento> findByAtendimentoIdAndVeiculoIdAndServicoId(
            UUID atendimentoId,
            UUID veiculoId,
            UUID servicoId
    );

    Optional<ItemAtendimento> findByIdAndAtendimentoId(UUID id, UUID atendimentoId);

    List<ItemAtendimento> findAllByAtendimentoId(UUID atendimentoId);
}
