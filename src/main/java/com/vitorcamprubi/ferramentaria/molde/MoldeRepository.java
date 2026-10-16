package com.vitorcamprubi.ferramentaria.molde;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MoldeRepository extends JpaRepository<Molde, Long> {

    boolean existsByCodigo(String codigo);
}
