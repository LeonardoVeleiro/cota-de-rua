package br.com.cotaderua.service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cotaderua.dto.AlertaResponse;
import br.com.cotaderua.dto.AnaliseAlerta;
import br.com.cotaderua.model.Medicao;
import br.com.cotaderua.model.PontoMonitorado;
import br.com.cotaderua.repository.MedicaoRepository;
import br.com.cotaderua.repository.PontoMonitoradoRepository;

@Service
public class AlertaService {

    private final PontoMonitoradoRepository pontoRepository;
    private final MedicaoRepository medicaoRepository;
    private final ClassificadorAlerta classificador;

    public AlertaService(
            PontoMonitoradoRepository pontoRepository,
            MedicaoRepository medicaoRepository,
            ClassificadorAlerta classificador) {
        this.pontoRepository = pontoRepository;
        this.medicaoRepository = medicaoRepository;
        this.classificador = classificador;
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listar() {
        OffsetDateTime agora = OffsetDateTime.now();

        return pontoRepository.findAll()
            .stream()
            .map(ponto -> analisarPonto(ponto, agora))
            .sorted(
                Comparator.comparingInt(AlertaResponse::pontuacao)
                    .reversed()
                    .thenComparing(AlertaResponse::nomePonto)
            )
            .toList();
    }

    private AlertaResponse analisarPonto(
            PontoMonitorado ponto,
            OffsetDateTime agora) {

        return medicaoRepository
            .findFirstByPonto_IdOrderByHorarioDescIdDesc(ponto.getId())
            .map(medicao -> montarAlerta(ponto, medicao, agora))
            .orElseGet(() -> new AlertaResponse(
                ponto.getId(),
                ponto.getNome(),
                ponto.getBairro(),
                null,
                null,
                null,
                null,
                null,
                "SEM_DADOS",
                0,
                "Este ponto ainda não recebeu medições.",
                false,
                ponto.isSimulado()
            ));
    }

    private AlertaResponse montarAlerta(
            PontoMonitorado ponto,
            Medicao medicao,
            OffsetDateTime agora) {

        AnaliseAlerta analise = classificador.analisar(
            medicao.getNivelAguaCm(),
            medicao.getHorario(),
            agora
        );

        return new AlertaResponse(
            ponto.getId(),
            ponto.getNome(),
            ponto.getBairro(),
            medicao.getId(),
            medicao.getNivelAguaCm(),
            medicao.getHorario(),
            medicao.getOrigem(),
            medicao.getConfianca(),
            analise.classificacao(),
            analise.pontuacao(),
            analise.motivo(),
            analise.desatualizado(),
            medicao.isSimulado()
        );
    }
}