package org.example.stockdashboard.scheduler;

import org.example.stockdashboard.service.BitcoinService;
import org.example.stockdashboard.service.OnchainMetricsService;
import org.example.stockdashboard.service.RiskMetricsService;
import org.example.stockdashboard.service.TechnicalIndicatorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;


@Component
public class BitcoinDataScheduler {
    private final BitcoinService bitcoinService;
    private final TechnicalIndicatorService technicalIndicatorService;
    private final OnchainMetricsService onchainMetricsService;
    private final RiskMetricsService riskMetricsService;

    public BitcoinDataScheduler(BitcoinService bitcoinService, TechnicalIndicatorService technicalIndicatorService, OnchainMetricsService onchainMetricsService, RiskMetricsService riskMetricsService) {
        this.bitcoinService = bitcoinService;
        this.technicalIndicatorService = technicalIndicatorService;
        this.onchainMetricsService = onchainMetricsService;
        this.riskMetricsService = riskMetricsService;
    }

    //5분마다 가격 데이터 업데이트
    @Scheduled(fixedRate = 300000)
    public void updatePriceData() {
        try {
            bitcoinService.updateCurrentPrice();
            System.out.println("Price data updated at:" + java.time.LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error updating price data:" +e.getMessage());
        }
    }

    //1시간마다 뉴스 데이터 업데이트
    @Scheduled(fixedRate = 3600000)
    public void updateNewsData() {
        try {
            bitcoinService.updateNewsFromAPI();
            System.out.println("News data updated at: " +java.time.LocalDateTime.now());
        }catch (Exception e) {
            System.err.println("Error updating news data: " +e.getMessage());
            e.printStackTrace();
        }
    }
    // 10분마다 기술적 지표 업데이트
    @Scheduled(fixedRate = 600000)
    public void updateTechnicalIndicators() {
        try {
            technicalIndicatorService.fetchAndUpdateTechnicalIndicators();
            System.out.println("Technical indicators updated at: " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error updating technical indicators: " + e.getMessage());
        }
    }

    // 15분마다 온체인 메트릭 업데이트
    @Scheduled(fixedRate = 900000)
    public void updateOnchainMetrics() {
        try {
            onchainMetricsService.fetchAndUpdateOnchainMetrics();
            System.out.println("Onchain metrics updated at: " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error updating onchain metrics: " + e.getMessage());
        }
    }

    // 20분마다 리스크 메트릭 업데이트
    @Scheduled(fixedRate = 1200000)
    public void updateRiskMetrics() {
        try {
            riskMetricsService.fetchAndUpdateRiskMetrics();
            System.out.println("Risk metrics updated at: " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error updating risk metrics: " + e.getMessage());
        }
    }
}
