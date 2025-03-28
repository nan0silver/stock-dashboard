package org.example.stockdashboard.model.repository;

import org.example.stockdashboard.model.dto.BitcoinPrice;

import java.util.List;

public interface BitcoinRepository extends JDBCRepository{
    BitcoinPrice getLastestPrice() throws Exception;
    void savePrice(BitcoinPrice price) throws Exception;
    List<BitcoinPrice> getPriceHistory(int linit) throws Exception;
}
