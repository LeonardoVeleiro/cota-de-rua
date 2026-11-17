package br.com.cotaderua.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cotaderua.model.Relato;

public interface RelatoRepository extends JpaRepository<Relato, Long> {

    List<Relato> findTop20ByPonto_IdOrderByHorarioDescIdDesc(Long pontoId);
}