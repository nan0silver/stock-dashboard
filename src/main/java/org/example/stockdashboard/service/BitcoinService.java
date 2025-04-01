package org.example.stockdashboard.service;

import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;

import java.util.List;
import java.util.Map;

public interface BitcoinService {
    BitcoinPriceDto getCurrentPrice() throws Exception;
    List<BitcoinPriceDto> getPriceHistory(int limit) throws Exception;
    List<BitcoinNews> getLatestNews(int limit) throws Exception;
    Map<String, Object> getTechnicalIndicators() throws Exception;
    Map<String, Object> getOnchainMetrics() throws Exception;
    Map<String, Object> getRiskMetrics() throws Exception;
}