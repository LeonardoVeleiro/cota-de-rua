package br.com.cotaderua;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import br.com.cotaderua.service.ClassificadorAlerta;

import static org.junit.jupiter.api.Assertions.*;

class ClassificadorAlertaTest {

    private final ClassificadorAlerta classificador =
        new ClassificadorAlerta();

    private final OffsetDateTime agora =
        OffsetDateTime.parse("2026-11-17T16:00:00-03:00");

    @Test
    void deveClassificarOsLimitesDeCadaFaixa() {
        assertEquals("BAIXO",
            classificador.analisar(9.99, agora, agora).classificacao());

        assertEquals("ATENCAO",
            classificador.analisar(10, agora, agora).classificacao());

        assertEquals("ATENCAO",
            classificador.analisar(19.99, agora, agora).classificacao());

        assertEquals("ALTO",
            classificador.analisar(20, agora, agora).classificacao());

        assertEquals("ALTO",
            classificador.analisar(39.99, agora, agora).classificacao());

        assertEquals("CRITICO",
            classificador.analisar(40, agora, agora).classificacao());
    }

    @Test
    void deveManterAlertaCriticoQuandoMedicaoFicarAntiga() {
        var resultado = classificador.analisar(
            45, agora.minusMinutes(6), agora
        );

        assertEquals("CRITICO", resultado.classificacao());
        assertEquals(100, resultado.pontuacao());
        assertTrue(resultado.desatualizado());
        assertTrue(resultado.motivo().contains("último registro"));
    }

    @Test
    void deveConsiderarCincoMinutosExatosComoAtualizado() {
        var resultado = classificador.analisar(
            12, agora.minusMinutes(5), agora
        );

        assertFalse(resultado.desatualizado());
    }

    @Test
    void deveMarcarComoAntigaAposCincoMinutos() {
        var resultado = classificador.analisar(
            12, agora.minusMinutes(5).minusSeconds(1), agora
        );

        assertTrue(resultado.desatualizado());
    }

    @Test
    void deveRejeitarNiveisInvalidos() {
        for (double nivel : new double[]{
                -1, 501, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(
                IllegalArgumentException.class,
                () -> classificador.analisar(nivel, agora, agora)
            );
        }
    }

    @Test
    void deveRejeitarMedicaoNoFuturo() {
        assertThrows(
            IllegalArgumentException.class,
            () -> classificador.analisar(
                10, agora.plusMinutes(1), agora
            )
        );
    }
}