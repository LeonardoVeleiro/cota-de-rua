package br.com.cotaderua;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public Map<String, String> status() {
        return Map.of(
            "projeto", "Cota de Rua",
            "status", "ONLINE",
            "mensagem", "Monitoramento de alagamentos em funcionamento",
            "horario", OffsetDateTime.now().toString()
        );
    }
}