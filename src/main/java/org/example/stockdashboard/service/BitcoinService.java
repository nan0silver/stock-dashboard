package org.example.stockdashboard.service;

import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;

import java.util.List;

public interface BitcoinService {
    BitcoinPriceDto getCurrentPrice() throws Exception;
    List<BitcoinPriceDto> getPriceHistory(int limit) throws Exception;
    List<BitcoinNews> getLatestNews(int limit) throws Exception;
}