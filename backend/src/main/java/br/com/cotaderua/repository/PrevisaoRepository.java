package br.com.cotaderua.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cotaderua.model.PrevisaoSalva;

public interface PrevisaoRepository
        extends JpaRepository<PrevisaoSalva, Long> {

    Optional<PrevisaoSalva> findFirstByOrderByConsultadoEmDescIdDesc();
}