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
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    public static class Coordinates {
        public double lat;
        public double lon;
    }

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

    /**
     * 주소 문자열을 위도/경도로 변환합니다. (OpenWeatherMap Geocoding API 사용)
     * (주의: 이 함수는 WebClient를 '차단(blocking)' 방식으로 호출합니다)
     */
    public Coordinates getCoordinatesFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }

        try {
            // OpenWeatherMap Geocoding API 엔드포인트: /geo/1.0/direct
            Mono<JsonNode> responseMono = this.webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/geo/1.0/direct")
                            .queryParam("q", address) // ⬅️ 주소 전달
                            .queryParam("limit", 1)   // ⬅️ 가장 정확한 1개만 받기
                            .queryParam("appid", this.apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class); // ⬅️ 응답이 JSON 배열로 옴

            // ❗️ 중요: Mono를 동기식으로 실행 (JPA Service에서 호출하기 위함)
            JsonNode responseArray = responseMono.block();

            // 3. 응답 파싱
            if (responseArray != null && responseArray.isArray() && responseArray.size() > 0) {
                JsonNode geoData = responseArray.get(0); // 배열의 첫 번째 결과 사용
                Coordinates coords = new Coordinates();
                coords.lat = geoData.path("lat").asDouble();
                coords.lon = geoData.path("lon").asDouble();

                log.info(" Geocoding 성공: {} -> [{}, {}]", address, coords.lat, coords.lon);
                return coords;
            } else {
                log.warn(" Geocoding 실패: 주소 '{}'에 대한 결과를 찾을 수 없음", address);
                return null;
            }
        } catch (Exception e) {
            log.error(" Geocoding API 호출 중 예외 발생: {}", e.getMessage());
            return null;
        }
    }


}

