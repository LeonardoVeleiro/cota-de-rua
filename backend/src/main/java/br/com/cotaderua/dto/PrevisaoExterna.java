package br.com.cotaderua.dto;

import java.util.List;

public record PrevisaoExterna(
    Daily daily
) {
    public record Daily(
        List<String> time,
        List<Double> precipitation_sum,
        List<Integer> precipitation_probability_max
    ) {
    }
}