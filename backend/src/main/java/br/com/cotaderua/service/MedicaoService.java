package br.com.cotaderua.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.cotaderua.dto.MedicaoRequest;
import br.com.cotaderua.dto.MedicaoResponse;
import br.com.cotaderua.model.Medicao;
import br.com.cotaderua.model.PontoMonitorado;
import br.com.cotaderua.repository.MedicaoRepository;
import br.com.cotaderua.repository.PontoMonitoradoRepository;

@Service
public class MedicaoService {

    private final MedicaoRepository medicaoRepository;
    private final PontoMonitoradoRepository pontoRepository;

    public MedicaoService(
            MedicaoRepository medicaoRepository,
            PontoMonitoradoRepository pontoRepository) {
        this.medicaoRepository = medicaoRepository;
        this.pontoRepository = pontoRepository;
    }

    @Transactional
    public MedicaoResponse registrarSimulada(MedicaoRequest request) {
        PontoMonitorado ponto = buscarPonto(request.pontoId());

        if (!ponto.isSimulado()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A simulação só pode registrar dados em pontos simulados."
            );
        }

        Double nivel = request.nivelAguaCm();

        if (nivel == null || !Double.isFinite(nivel)
                || nivel < 0 || nivel > 500) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Informe um nível entre 0 e 500 cm."
            );
        }

        // Pontuação fixa de demonstração, sem calibração estatística.
        int confiancaSimulada = 80;

        Medicao medicao = new Medicao(
            ponto,
            nivel,
            OffsetDateTime.now(),
            "SENSOR_SIMULADO",
            confiancaSimulada,
            true
        );

        return MedicaoResponse.de(medicaoRepository.save(medicao));
    }

    @Transactional(readOnly = true)
    public List<MedicaoResponse> listarPorPonto(Long pontoId) {
        buscarPonto(pontoId);

        return medicaoRepository
            .findTop20ByPonto_IdOrderByHorarioDescIdDesc(pontoId)
            .stream()
            .map(MedicaoResponse::de)
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