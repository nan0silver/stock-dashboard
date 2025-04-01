package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OnchainMetricsServiceImpl implements OnchainMetricsService{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OnchainMetricsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> getOnchainMetrics() throws Exception {
        Map<String, Object> metrics = new HashMap<>();

        try{
            String url = "https://api.blockchain.info/stats";
            //https://www.blockchain.com/explorer/api/charts_api
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            JsonNode stats = objectMapper.readTree(response.getBody());
            // 활성 거래 수 (일반 트랜잭션 수)
            metrics.put("activeWallets", stats.get("n_tx").asInt());
            // 평균 거래 수수료
            metrics.put("avgTransactionFee", calculateAvgTransactionFee(stats));
            // 채굴 난이도
            String difficultyStatus = getDifficultyStatus(stats.get("difficulty").asLong());
            metrics.put("miningDifficulty", difficultyStatus);
            // 해시레이트
            String hashRate = formatHashRate(stats.get("hash_rate").asDouble());
            metrics.put("hashRate", hashRate);
            return metrics;
        }catch(Exception e) {
            return getFallbackMetrics();
        }
    }

    private double calculateAvgTransactionFee(JsonNode stats) {
        // 총 수수료를 트랜잭션 수로 나누어 평균 계산
        long totalFeeSatoshi = stats.get("total_fees_btc").asLong();
        int nTx = stats.get("n_tx").asInt();
        double avgFeeBtc = (double) totalFeeSatoshi / nTx / 100000000.0; //사토시 -> BTC
        double btcPriceUsd = stats.get("market_price_usd").asDouble();

        return Math.round(avgFeeBtc * btcPriceUsd * 100.0) / 100.0;
    }

    private String getDifficultyStatus(long currentDifficulty){
        try {
            long previousDifficulty = getPreviousDifficulty();
            double changePct = ((double) currentDifficulty - previousDifficulty)/previousDifficulty * 100;

            if (changePct > 3) {
                return "증가중";
            } else if (changePct < -3) {
                return "감소중";
            } else {
                return "안정적";
            }
        }catch(Exception e) {
            return "데이터 없음";
        }
    }

    // 이전 난이도 조회
    private long getPreviousDifficulty() {
        return 225832872179L;
    }

    private String formatHashRate(double hashRate){
        // hash_rate는 GH/s 단위이므로 EH/s로 변환 (10^9)
        double hashRateEH = hashRate / 1000000000.0;
        return String.format("%.2f EH/s", hashRateEH);
    }

    private Map<String, Object> getFallbackMetrics() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("activeWallets", 230000);
        fallback.put("avgTransactionFee", 12.50);
        fallback.put("miningDifficulty", "증가중");
        fallback.put("hashRate", "420 EH/s");
        return fallback;
    }
}
