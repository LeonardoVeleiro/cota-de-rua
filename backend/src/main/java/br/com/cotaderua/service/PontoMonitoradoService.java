package br.com.cotaderua.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cotaderua.model.PontoMonitorado;
import br.com.cotaderua.repository.PontoMonitoradoRepository;

@Service
public class PontoMonitoradoService {

    private final PontoMonitoradoRepository repository;

    public PontoMonitoradoService(PontoMonitoradoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PontoMonitorado> listar() {
        return repository.findAll(Sort.by("nome"));
    }
}