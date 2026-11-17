package br.com.cotaderua.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cotaderua.model.Medicao;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findTop20ByPonto_IdOrderByHorarioDescIdDesc(Long pontoId);

    Optional<Medicao> findFirstByPonto_IdOrderByHorarioDescIdDesc(Long pontoId);
}