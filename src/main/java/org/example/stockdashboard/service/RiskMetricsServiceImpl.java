package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.util.DotenvMixin;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RiskMetricsServiceImpl implements RiskMetricsService, DotenvMixin {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public RiskMetricsServiceImpl(WebClient.Builder webClientBuilder, ObjectMapper objectMapper){
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> getRiskMetrics() throws Exception {
        Map<String, Object> metrics = new HashMap<>();

        try {
            // Fear & Greed 인덱스 가져오기

            JsonNode fearGreedData = fetchFearGreedIndex()
                    .timeout(Duration.ofSeconds(10))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .onErrorResume(e -> {
                        System.out.println("Fear & Greed API 호출 실패: " + e.getMessage());
                        return Mono.empty();
                    })
                    .block();

            int fgIndex = 50;
            String fgClassification = "Neutral";

            if (fearGreedData != null && fearGreedData.has("data")){
                JsonNode fearGreedValue = fearGreedData.get("data").get(0);
                fgIndex = fearGreedValue.get("value").asInt();
                fgClassification = fearGreedValue.get("value_classification").asText();
            }

            metrics.put("fearGreedIndex", fgIndex);
            metrics.put("marketSentiment", fgClassification);



            return metrics;
        } catch (Exception e){
            System.err.println("리스크 지표 가져오기 실패 : " +e.getMessage());
            e.printStackTrace();
            return metrics;
        }
    }

    private Mono<JsonNode> fetchFearGreedIndex() {
        //https://alternative.me/crypto/fear-and-greed-index/#api
        String fearGreedUrl = "https://api.alternative.me/fng/";

        return webClient.get()
                .uri(fearGreedUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try{
                        return objectMapper.readTree(response);
                    } catch (Exception e) {
                        throw new RuntimeException("Fear & Greed 응답 파싱 오류", e);
                    }
                });
    }
}
