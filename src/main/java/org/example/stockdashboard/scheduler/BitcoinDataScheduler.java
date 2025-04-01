package org.example.stockdashboard.scheduler;

import org.example.stockdashboard.service.BitcoinService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class BitcoinDataScheduler {
    private final BitcoinService bitcoinService;

    public BitcoinDataScheduler(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    //5분마다 가격 데이터 업데이트
    @Scheduled(fixedRate = 300000)
    public void updatePriceData() {
        try {
            bitcoinService.getCurrentPrice();
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
}
