package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.model.repository.RiskMetricsRepository;
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
    private final TechnicalIndicatorService technicalIndicatorService;
    private final RiskMetricsRepository riskMetricsRepository;

    public RiskMetricsServiceImpl(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, TechnicalIndicatorService technicalIndicatorService, RiskMetricsRepository riskMetricsRepository){
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.technicalIndicatorService = technicalIndicatorService;
        this.riskMetricsRepository = riskMetricsRepository;
    }

    @Override
    public Map<String, Object> fetchAndUpdateRiskMetrics() throws Exception {
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

            // 기술 지표 서비스에서 가격 데이터 가져오기 (API)
            Map<String, Object> technicalData = technicalIndicatorService.fetchAndUpdateTechnicalIndicators();

            if (technicalData.containsKey("volatility30d")) {
                metrics.put("volatility30d", technicalData.get("volatility30d"));
            }else {
                metrics.put("volatility30d", 4.2);
            }

            if (technicalData.containsKey("priceChangeRisk")) {
                metrics.put("priceChangeRisk", technicalData.get("priceChangeRisk"));
            }else {
                metrics.put("priceChangeRisk", "중간");
            }

            // 시장 건정성
            double volatility = (double) metrics.get("volatility30d");
            String marketHealth = getMarketHealth(fgIndex, volatility);
            metrics.put("marketHealth", marketHealth);

            //DB에 저장
            riskMetricsRepository.saveRiskMetrics(metrics);

            return metrics;
        } catch (Exception e){
            System.err.println("리스크 지표 가져오기 실패 : " +e.getMessage());
            e.printStackTrace();
            return metrics;
        }
    }

    @Override
    public Map<String, Object> getRiskMetrics() throws Exception {
        Map<String, Object> metrics = riskMetricsRepository.getLatestRiskMetrics();

        if (metrics.isEmpty()) {
            metrics = fetchAndUpdateRiskMetrics();
        }
        return metrics;
    }

    private String getMarketHealth(int fearGreedIndex, double volatility) {
        if (fearGreedIndex < 25 || volatility > 6.0) {
            return "불안정";
        } else if (fearGreedIndex > 60 && volatility < 3.0) {
            return "매우 양호";
        } else if (fearGreedIndex > 50 || volatility < 4.0) {
            return "양호";
        } else {
            return "보통";
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
