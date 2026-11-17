package br.com.cotaderua.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.cotaderua.model.PontoMonitorado;
import br.com.cotaderua.repository.PontoMonitoradoRepository;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final PontoMonitoradoRepository repository;

    public DadosIniciais(PontoMonitoradoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        repository.saveAll(List.of(
            new PontoMonitorado(
                "Travessa do Mercado",
                "Vila Aurora (fictício)",
                "Trecho em frente às lojas. Local fictício para demonstração.",
                true
            ),
            new PontoMonitorado(
                "Passagem do Córrego",
                "Vila Aurora (fictício)",
                "Travessia de pedestres. Local fictício para demonstração.",
                true
            ),
            new PontoMonitorado(
                "Rua da Escola",
                "Vila Aurora (fictício)",
                "Trecho próximo ao portão da escola. Local fictício para demonstração.",
                true
            )
        ));
    }
}