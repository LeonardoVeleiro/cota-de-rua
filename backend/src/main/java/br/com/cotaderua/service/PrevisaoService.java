package br.com.cotaderua.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import br.com.cotaderua.dto.PrevisaoExterna;
import br.com.cotaderua.model.PrevisaoSalva;
import br.com.cotaderua.repository.PrevisaoRepository;

@Service
public class PrevisaoService {

    private static final Logger LOG =
        LoggerFactory.getLogger(PrevisaoService.class);

    private static final ZoneId FUSO =
        ZoneId.of("America/Sao_Paulo");

    private final RestClient cliente;
    private final PrevisaoRepository repository;

    private OffsetDateTime tentarNovamenteEm;
    private boolean ultimaTentativaFalhou;

    public PrevisaoService(PrevisaoRepository repository) {
        this.repository = repository;

        SimpleClientHttpRequestFactory fabrica =
            new SimpleClientHttpRequestFactory();

        fabrica.setConnectTimeout(3000);
        fabrica.setReadTimeout(5000);

        cliente = RestClient.builder()
            .baseUrl("https://api.open-meteo.com")
            .requestFactory(fabrica)
            .build();
    }

    public synchronized Map<String, Object> consultar() {
        OffsetDateTime agora = OffsetDateTime.now(FUSO);

        PrevisaoSalva salva = repository
            .findFirstByOrderByConsultadoEmDescIdDesc()
            .orElse(null);

        if (tentarNovamenteEm != null
                && agora.isBefore(tentarNovamenteEm)) {
            if (salva != null) {
                return montarResposta(
                    salva,
                    ultimaTentativaFalhou || !estaAtualizada(salva, agora)
                );
            }

            throw indisponivel();
        }

        if (salva != null && estaAtualizada(salva, agora)) {
            ultimaTentativaFalhou = false;
            return montarResposta(salva, false);
        }

        PrevisaoSalva nova;

        try {
            PrevisaoExterna resposta = cliente.get()
                .uri(uri -> uri
                    .path("/v1/forecast")
                    .queryParam("latitude", -23.5505)
                    .queryParam("longitude", -46.6333)
                    .queryParam(
                        "daily",
                        "precipitation_sum,precipitation_probability_max"
                    )
                    .queryParam("timezone", "America/Sao_Paulo")
                    .queryParam("forecast_days", 1)
                    .build()
                )
                .retrieve()
                .body(PrevisaoExterna.class);

            nova = converter(resposta);

        } catch (Exception erro) {
            LOG.warn(
                "Falha ao consultar a Open-Meteo: {}",
                erro.getMessage()
            );

            ultimaTentativaFalhou = true;
            tentarNovamenteEm = OffsetDateTime.now(FUSO).plusMinutes(1);

            if (salva != null) {
                return montarResposta(salva, true);
            }

            throw indisponivel();
        }

        // Salva uma nova consulta para manter o histórico no banco.
        PrevisaoSalva registrada = repository.save(nova);

        ultimaTentativaFalhou = false;
        tentarNovamenteEm = null;

        return montarResposta(registrada, false);
    }

    private boolean estaAtualizada(
            PrevisaoSalva previsao,
            OffsetDateTime agora) {

        boolean mesmoDia = previsao.getDataPrevisao()
            .equals(agora.toLocalDate().toString());

        boolean consultaRecente =
            !previsao.getConsultadoEm().isAfter(agora)
            && agora.isBefore(
                previsao.getConsultadoEm().plusMinutes(15)
            );

        return mesmoDia && consultaRecente;
    }

    private PrevisaoSalva converter(PrevisaoExterna resposta) {
        if (resposta == null
                || resposta.daily() == null
                || resposta.daily().time() == null
                || resposta.daily().time().isEmpty()
                || resposta.daily().precipitation_sum() == null
                || resposta.daily().precipitation_sum().isEmpty()
                || resposta.daily().precipitation_probability_max() == null
                || resposta.daily().precipitation_probability_max().isEmpty()) {
            throw new IllegalStateException("Previsão incompleta.");
        }

        String data = resposta.daily().time().get(0);
        Double chuva = resposta.daily().precipitation_sum().get(0);
        Integer probabilidade = resposta.daily()
            .precipitation_probability_max().get(0);

        if (data == null || chuva == null || probabilidade == null
                || !Double.isFinite(chuva) || chuva < 0
                || probabilidade < 0 || probabilidade > 100) {
            throw new IllegalStateException("Valores inválidos.");
        }

        if (!LocalDate.parse(data).equals(LocalDate.now(FUSO))) {
            throw new IllegalStateException(
                "A fonte não retornou a previsão do dia atual."
            );
        }

        return new PrevisaoSalva(
            data,
            chuva,
            probabilidade,
            OffsetDateTime.now(FUSO)
        );
    }

    private Map<String, Object> montarResposta(
            PrevisaoSalva previsao,
            boolean desatualizado) {

        Map<String, Object> dados = new LinkedHashMap<>();

        dados.put("id", previsao.getId());
        dados.put("local", previsao.getLocalReferencia());
        dados.put("dataPrevisao", previsao.getDataPrevisao());
        dados.put("precipitacaoPrevistaMm", previsao.getPrecipitacaoMm());
        dados.put(
            "probabilidadeMaximaChuvaPercentual",
            previsao.getProbabilidadeChuva()
        );
        dados.put("origem", previsao.getOrigem());
        dados.put("fonteUrl", "https://open-meteo.com/");
        dados.put("tipo", "PREVISAO_METEOROLOGICA");
        dados.put("confianca", "NAO_QUANTIFICADA");
        dados.put(
            "justificativaConfianca",
            "Previsão de modelo meteorológico. "
            + "Probabilidade de chuva não é confiança do dado "
            + "nem probabilidade de alagamento."
        );
        dados.put(
            "consultadoEm",
            previsao.getConsultadoEm().toString()
        );
        dados.put("desatualizado", desatualizado);
        dados.put("simulado", false);
        dados.put(
            "observacao",
            desatualizado
                ? "Não foi possível atualizar a previsão. "
                    + "Exibindo o último registro salvo; confira a data."
                : "Contexto regional para a demonstração. "
                    + "Não representa medição nos pontos fictícios."
        );

        return dados;
    }

    private ResponseStatusException indisponivel() {
        return new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "A fonte de previsão está indisponível "
            + "e ainda não há uma consulta salva."
        );
    }
}