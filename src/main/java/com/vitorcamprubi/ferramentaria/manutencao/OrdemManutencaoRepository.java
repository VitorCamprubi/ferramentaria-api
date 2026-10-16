package com.vitorcamprubi.ferramentaria.manutencao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemManutencaoRepository extends JpaRepository<OrdemManutencao, Long> {

    // O @EntityGraph traz o molde no mesmo SELECT e evita uma consulta extra por ordem (N+1).
    @EntityGraph(attributePaths = "molde")
    List<OrdemManutencao> findByStatus(StatusOrdem status);

    @EntityGraph(attributePaths = "molde")
    List<OrdemManutencao> findAllBy();

    @EntityGraph(attributePaths = "molde")
    Optional<OrdemManutencao> findWithMoldeById(Long id);
}
