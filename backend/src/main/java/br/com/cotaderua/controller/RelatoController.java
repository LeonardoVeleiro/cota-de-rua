package br.com.cotaderua.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotaderua.dto.RelatoRequest;
import br.com.cotaderua.dto.RelatoResponse;
import br.com.cotaderua.service.RelatoService;

@RestController
@RequestMapping("/api")
public class RelatoController {

    private final RelatoService service;

    public RelatoController(RelatoService service) {
        this.service = service;
    }

    @PostMapping("/relatos")
    @ResponseStatus(HttpStatus.CREATED)
    public RelatoResponse registrar(
            @Valid @RequestBody RelatoRequest request) {
        return service.registrar(request);
    }

    @GetMapping("/pontos/{pontoId}/relatos")
    public List<RelatoResponse> listarPorPonto(
            @PathVariable("pontoId") Long pontoId) {
        return service.listarPorPonto(pontoId);
    }
}