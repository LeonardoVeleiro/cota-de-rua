package br.com.cotaderua.service;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import br.com.cotaderua.dto.AnaliseAlerta;

@Service
public class ClassificadorAlerta {

    public AnaliseAlerta analisar(
            double nivelAguaCm,
            OffsetDateTime horarioMedicao,
            OffsetDateTime agora) {

        if (!Double.isFinite(nivelAguaCm)
                || nivelAguaCm < 0 || nivelAguaCm > 500) {
            throw new IllegalArgumentException(
                "O nível deve estar entre 0 e 500 cm."
            );
        }

        if (horarioMedicao == null || agora == null) {
            throw new IllegalArgumentException(
                "Os horários são obrigatórios."
            );
        }

        if (horarioMedicao.isAfter(agora)) {
            throw new IllegalArgumentException(
                "A medição não pode estar no futuro."
            );
        }

        String classificacao;
        int pontuacao;
        String motivo;

        if (nivelAguaCm >= 40) {
            classificacao = "CRITICO";
            pontuacao = 100;
            motivo = "Nível igual ou superior a 40 cm.";
        } else if (nivelAguaCm >= 20) {
            classificacao = "ALTO";
            pontuacao = 70;
            motivo = "Nível entre 20 e menos de 40 cm.";
        } else if (nivelAguaCm >= 10) {
            classificacao = "ATENCAO";
            pontuacao = 40;
            motivo = "Nível entre 10 e menos de 20 cm.";
        } else {
            classificacao = "BAIXO";
            pontuacao = 10;
            motivo = "Nível inferior a 10 cm.";
        }

        boolean desatualizado = Duration
            .between(horarioMedicao, agora)
            .compareTo(Duration.ofMinutes(5)) > 0;

        if (desatualizado) {
            motivo += " Medição sem atualização há mais de 5 minutos; "
                + "a classificação representa o último registro.";
        }

        return new AnaliseAlerta(
            classificacao,
            pontuacao,
            motivo,
            desatualizado
        );
    }
}