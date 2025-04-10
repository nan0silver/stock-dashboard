package org.example.stockdashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.model.dto.SentimentAnalysisResult;
import org.example.stockdashboard.model.repository.BitcoinRepositoryImpl;
import org.example.stockdashboard.service.BitcoinService;
import org.example.stockdashboard.service.OnchainMetricsService;
import org.example.stockdashboard.service.RiskMetricsService;
import org.example.stockdashboard.service.TechnicalIndicatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/")
public class BitcoinController {

    private final BitcoinService bitcoinService;
    private final ObjectMapper objectMapper;
    private final TechnicalIndicatorService technicalIndicatorService;
    private final OnchainMetricsService onchainMetricsService;
    private final RiskMetricsService riskMetricsService;
    private final BitcoinRepositoryImpl bitcoinRepositoryImpl;

    public BitcoinController(BitcoinService bitcoinService,
                             TechnicalIndicatorService technicalIndicatorService,
                             OnchainMetricsService onchainMetricsService,
                             RiskMetricsService riskMetricsService, BitcoinRepositoryImpl bitcoinRepositoryImpl) {
        this.bitcoinService = bitcoinService;
        this.technicalIndicatorService = technicalIndicatorService;
        this.onchainMetricsService = onchainMetricsService;
        this.riskMetricsService = riskMetricsService;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.bitcoinRepositoryImpl = bitcoinRepositoryImpl;
    }

    @GetMapping
    public String dashboard(Model model) throws Exception {
        //현재 비트코인 가격 조회
        CompletableFuture<BitcoinPriceDto> priceFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return bitcoinService.getCurrentPrice();
            } catch (Exception e) {
                return BitcoinPriceDto.of(BigDecimal.ZERO, BigDecimal.ZERO);
            }
        });

        //가격 이력 조회
        CompletableFuture<List<BitcoinPriceDto>> priceHistoryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return bitcoinService.getPriceHistory(30);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });

        //뉴스 데이터 추가
        CompletableFuture<List<BitcoinNews>> newsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return bitcoinService.getLatestNews(5);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });



        // 모든 CompletableFuture가 완료될 때까지 대기
        BitcoinPriceDto currentPrice = priceFuture.get();
        List<BitcoinPriceDto> priceHistory = priceHistoryFuture.get();
        List<BitcoinNews> latestNews = newsFuture.get();



        model.addAttribute("currentPrice", currentPrice);
        model.addAttribute("priceHistory", priceHistory);
        model.addAttribute("latestNews", latestNews);

        //차트 데이터 생성
        String priceHistoryJson = objectMapper.writeValueAsString(priceHistory);
        model.addAttribute("priceHistoryJson", priceHistoryJson);

        // 예측 데이터 생성 (샘플)
        List<Map<String, Object>> predictionData = generatePredictionData(currentPrice.price());
        model.addAttribute("predictionDataJson", objectMapper.writeValueAsString(predictionData));


        // 각 뉴스에 감정 분석 결과 추가
        List<Map<String, Object>> newsWithSentiment = new ArrayList<>();
        for (BitcoinNews news : latestNews) {
            String sentiment;

            // 저장된 감정 분석 결과가 있으면 사용, 없으면 API 호출
            if (news.sentiment() != null && !news.sentiment().isEmpty()){
                sentiment = news.sentiment();
            } else {
                sentiment = bitcoinService.analyzeSentiment(news.title());
                try {
                    bitcoinRepositoryImpl.updateNewsSentiment(news.id(), sentiment);
                }catch (Exception e) {
                    System.err.println("감정 분석 결과 저장 실패: " + e.getMessage());
                }
            }

            Map<String, Object> newsMap = new HashMap<>();
            newsMap.put("news", news);
            newsMap.put("sentiment", sentiment);
            newsWithSentiment.add(newsMap);
        }
        model.addAttribute("newsWithSentiment", newsWithSentiment);

        // 감성 분석 데이터
        List<SentimentAnalysisResult> sentimentData = bitcoinService.getNewsSentiment(7);
        model.addAttribute("sentimentDataJson", objectMapper.writeValueAsString(sentimentData));

        // 기술적 지표 (DB에서만)
        Map<String, Object> technicalIndicators = technicalIndicatorService.getTechnicalIndicators();
        model.addAttribute("technicalIndicators", technicalIndicators);

        // 온체인 분석 (DB에서만)
        Map<String, Object> onchainMetrics = onchainMetricsService.getOnchainMetrics();
        model.addAttribute("onchainMetrics", onchainMetrics);

        // 리스크 지표 (샘플)
        Map<String, Object> riskMetrics = riskMetricsService.getRiskMetrics();
        model.addAttribute("riskMetrics", riskMetrics);

        return "bitcoin/dashboard";
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

}

