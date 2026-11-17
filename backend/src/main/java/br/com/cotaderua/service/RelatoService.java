package br.com.cotaderua.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.cotaderua.dto.RelatoRequest;
import br.com.cotaderua.dto.RelatoResponse;
import br.com.cotaderua.model.PontoMonitorado;
import br.com.cotaderua.model.Relato;
import br.com.cotaderua.repository.PontoMonitoradoRepository;
import br.com.cotaderua.repository.RelatoRepository;

@Service
public class RelatoService {

    private final RelatoRepository relatoRepository;
    private final PontoMonitoradoRepository pontoRepository;

    public RelatoService(
            RelatoRepository relatoRepository,
            PontoMonitoradoRepository pontoRepository) {
        this.relatoRepository = relatoRepository;
        this.pontoRepository = pontoRepository;
    }

    @Transactional
    public RelatoResponse registrar(RelatoRequest request) {
        PontoMonitorado ponto = buscarPonto(request.pontoId());

        String descricao = request.descricao() == null
            ? ""
            : request.descricao().strip();

        if (descricao.isBlank()
                || descricao.length() < 10
                || descricao.length() > 500) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A descrição deve ter entre 10 e 500 caracteres."
            );
        }

        Relato relato = new Relato(
            ponto,
            descricao,
            OffsetDateTime.now(),
            ponto.isSimulado()
        );

        return RelatoResponse.de(relatoRepository.save(relato));
    }

    @Transactional(readOnly = true)
    public List<RelatoResponse> listarPorPonto(Long pontoId) {
        buscarPonto(pontoId);

        return relatoRepository
            .findTop20ByPonto_IdOrderByHorarioDescIdDesc(pontoId)
            .stream()
            .map(RelatoResponse::de)
            .toList();
    }

    private PontoMonitorado buscarPonto(Long pontoId) {
        if (pontoId == null || pontoId <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Informe um ID de ponto válido."
            );
        }

        return pontoRepository.findById(pontoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ponto monitorado não encontrado."
            ));
    }
}