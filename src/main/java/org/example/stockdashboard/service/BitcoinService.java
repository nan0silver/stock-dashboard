package org.example.stockdashboard.service;

import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.model.dto.SentimentAnalysisResult;

import java.util.List;

public interface BitcoinService {
    // DB에서만 가격 조회
    BitcoinPriceDto getCurrentPriceFromDB() throws Exception;
    // API 호출해 가격 업데이트 (스케줄러용)
    BitcoinPriceDto updateCurrentPrice() throws Exception;
    // DB에서 가격 이력 조회
    List<BitcoinPriceDto> getPriceHistory(int limit) throws Exception;
    // DB에서 뉴스 조회
    List<BitcoinNews> getLatestNews(int limit) throws Exception;
    // API에서 뉴스 업데이트 (스케줄러용)
    void updateNewsFromAPI() throws Exception;
    // 감정분석 관련 메서드
    List<SentimentAnalysisResult> getNewsSentiment(int days) throws Exception;
    String analyzeSentiment(String text);
}