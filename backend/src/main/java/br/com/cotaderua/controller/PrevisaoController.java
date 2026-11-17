package br.com.cotaderua.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotaderua.service.PrevisaoService;

@RestController
@RequestMapping("/api/previsao")
public class PrevisaoController {

    private final PrevisaoService service;

    public PrevisaoController(PrevisaoService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> consultar() {
        return service.consultar();
    }
}