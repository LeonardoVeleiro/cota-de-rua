package br.com.cotaderua.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotaderua.model.PontoMonitorado;
import br.com.cotaderua.service.PontoMonitoradoService;

@RestController
@RequestMapping("/api/pontos")
public class PontoMonitoradoController {

    private final PontoMonitoradoService service;

    public PontoMonitoradoController(PontoMonitoradoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PontoMonitorado> listar() {
        return service.listar();
    }
}