package br.com.cotaderua.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cotaderua.model.PontoMonitorado;

public interface PontoMonitoradoRepository
        extends JpaRepository<PontoMonitorado, Long> {
}