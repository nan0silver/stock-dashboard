package org.example.stockdashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.service.BitcoinService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/bitcoin")
public class BitcoinController {

    private final BitcoinService bitcoinService;
    private final ObjectMapper objectMapper;

    public BitcoinController(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @GetMapping
    public String dashboard(Model model) throws Exception {
        //현재 비트코인 가격 조회
        BitcoinPriceDto currentPrice = bitcoinService.getCurrentPrice();
        model.addAttribute("currentPrice", currentPrice);

        //가격 이력 조회
        List<BitcoinPriceDto> priceHistory = bitcoinService.getPriceHistory(30);
        model.addAttribute("priceHistory", priceHistory);

        //차트 데이터 생성
        String priceHistoryJson = objectMapper.writeValueAsString(priceHistory);
        model.addAttribute("priceHistoryJson", priceHistoryJson);

        // 예측 데이터 생성 (샘플)
        List<Map<String, Object>> predictionData = generatePredictionData(currentPrice.price());
        model.addAttribute("predictionDataJson", objectMapper.writeValueAsString(predictionData));

        //뉴스 데이터 추가
        List<BitcoinNews> latestNews = bitcoinService.getLatestNews(5);
        model.addAttribute("latestNews", latestNews);

        // 감성 분석 데이터 (샘플)
        List<Map<String, Object>> sentimentData = generateSentimentData();
        model.addAttribute("sentimentDataJson", objectMapper.writeValueAsString(sentimentData));

        // 기술적 지표 (샘플)
        Map<String, Object> technicalIndicators = generateTechnicalIndicators();
        model.addAttribute("technicalIndicators", technicalIndicators);

        // 온체인 분석 (샘플)
        Map<String, Object> onchainMetrics = generateOnchainMetrics();
        model.addAttribute("onchainMetrics", onchainMetrics);

        // 리스크 지표 (샘플)
        Map<String, Object> riskMetrics = generateRiskMetrics();
        model.addAttribute("riskMetrics", riskMetrics);

        return "bitcoin/dashboard";
    }
    // 감성 분석 데이터 생성 (샘플)
    private List<Map<String, Object>> generateSentimentData() {
        List<Map<String, Object>> sentimentData = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        Random rand = new Random();

        for (int i = 6; i >= 0; i--) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", now.minusDays(i).toString());

            data.put("positive", 50 + rand.nextInt(30));
            data.put("negative", 20 + rand.nextInt(20));
            data.put("neutral", 20 + rand.nextInt(20));

            sentimentData.add(data);
        }

        return sentimentData;
    }

    // 가격 예측 데이터 생성 (샘플)
    private List<Map<String, Object>> generatePredictionData(BigDecimal currentPrice) {
        List<Map<String, Object>> predictions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Random rand = new Random();

        for (int i = 1; i <= 7; i++) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("date", now.plusDays(i).toString());

            BigDecimal predictedPrice = currentPrice.multiply(
                    BigDecimal.valueOf(1 + (0.01 * i) + (rand.nextDouble() * 0.02 - 0.01))
            );
            prediction.put("predicted", predictedPrice.intValue());
            prediction.put("lower", predictedPrice.multiply(BigDecimal.valueOf(0.97)).intValue());
            prediction.put("upper", predictedPrice.multiply(BigDecimal.valueOf(1.03)).intValue());

            predictions.add(prediction);
        }

        return predictions;
    }

    // 기술적 지표 생성 (샘플)
    private Map<String, Object> generateTechnicalIndicators() {
        Map<String, Object> indicators = new HashMap<>();

        indicators.put("rsi", 62.5);
        indicators.put("macd", "상승");
        indicators.put("bollingerBands", "상단 접근중");
        indicators.put("movingAverage200d", "상회");

        return indicators;
    }

    // 온체인 분석 데이터 생성 (샘플)
    private Map<String, Object> generateOnchainMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("activeWallets", 1250000);
        metrics.put("avgTransactionFee", 12.80);
        metrics.put("miningDifficulty", "증가중");
        metrics.put("hashRate", "325 EH/s");

        return metrics;
    }

    // 리스크 지표 생성 (샘플)
    private Map<String, Object> generateRiskMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("volatility30d", 4.2);
        metrics.put("priceChangeRisk", "중간");
        metrics.put("marketHealth", "양호");
        metrics.put("regulatoryRisk", "중간");

        return metrics;
    }
}

