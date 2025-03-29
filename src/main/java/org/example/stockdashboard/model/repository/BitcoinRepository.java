package org.example.stockdashboard.model.repository;

import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPrice;

import java.util.List;

public interface BitcoinRepository extends JDBCRepository{
    BitcoinPrice getLastestPrice() throws Exception;
    void savePrice(BitcoinPrice price) throws Exception;
    List<BitcoinPrice> getPriceHistory(int limit) throws Exception;
    void saveNews(BitcoinNews news) throws Exception;
    List<BitcoinNews> getLatestNews(int limit) throws Exception;
}
