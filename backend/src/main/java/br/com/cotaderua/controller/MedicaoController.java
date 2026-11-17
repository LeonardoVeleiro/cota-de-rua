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

import br.com.cotaderua.dto.MedicaoRequest;
import br.com.cotaderua.dto.MedicaoResponse;
import br.com.cotaderua.service.MedicaoService;

@RestController
@RequestMapping("/api")
public class MedicaoController {

    private final MedicaoService service;

    public MedicaoController(MedicaoService service) {
        this.service = service;
    }

    @PostMapping("/simulacao/medicoes")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicaoResponse registrar(
            @Valid @RequestBody MedicaoRequest request) {
        return service.registrarSimulada(request);
    }

    @GetMapping("/pontos/{pontoId}/medicoes")
    public List<MedicaoResponse> listarPorPonto(
            @PathVariable("pontoId") Long pontoId) {
        return service.listarPorPonto(pontoId);
    }
}