package com.Campmate.DYCampmate.service;


import com.Campmate.DYCampmate.dto.WeatherDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class WeatherService {

    private final WebClient webClient;
    private final String apiKey;


    public WeatherService(WebClient.Builder webClientBuilder,
                          @Value("${openweathermap.api.url}") String baseUrl,
                          @Value("${openweathermap.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }


    // 5일 3시간 간격 예보를 가져옵니다.
    public Mono<List<WeatherDTO>> getFiveDayForecast(String lat, String lon) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/forecast") // *** 변경된 엔드포인트 ***
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric") // 섭씨
                        .queryParam("lang", "kr")     // 한국어
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::parseForecastResponse); // 파싱 로직 변경
    }

    // '/forecast' API의 응답(JsonNode)을 DTO 리스트로 파싱
    private List<WeatherDTO> parseForecastResponse(JsonNode root) {
        // 응답 JSON의 "list" 필드를 순회 가능한 Stream으로 변환
        JsonNode listNode = root.path("list");

        return StreamSupport.stream(listNode.spliterator(), false)
                .map(itemNode -> {
                    WeatherDTO dto = new WeatherDTO();
                    dto.setDescription(itemNode.path("weather").get(0).path("description").asText());
                    dto.setIcon(itemNode.path("weather").get(0).path("icon").asText());
                    dto.setTemperature(itemNode.path("main").path("temp").asDouble());
                    dto.setHumidity(itemNode.path("main").path("humidity").asInt());
                    dto.setDt_txt(itemNode.path("dt_txt").asText());
                    return dto;
                })
                .collect(Collectors.toList()); // DTO의 List로 반환
    }

}

