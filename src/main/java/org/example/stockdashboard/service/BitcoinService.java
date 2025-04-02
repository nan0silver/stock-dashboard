package org.example.stockdashboard.service;

import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.model.dto.SentimentAnalysisResult;

import java.util.List;

public interface BitcoinService {
    BitcoinPriceDto getCurrentPrice() throws Exception;
    List<BitcoinPriceDto> getPriceHistory(int limit) throws Exception;
    List<BitcoinNews> getLatestNews(int limit) throws Exception;
    void updateNewsFromAPI() throws Exception;
    List<SentimentAnalysisResult> getNewsSentiment(int days) throws Exception;
    String analyzeSentiment(String text);
}