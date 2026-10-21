package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {
    Optional<EmpresaParceira> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    Optional<EmpresaParceira> findByEmail(String email);
    boolean existsByEmail(String email);
}

