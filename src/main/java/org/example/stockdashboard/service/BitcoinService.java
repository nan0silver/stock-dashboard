package org.example.stockdashboard.service;

import org.example.stockdashboard.model.dto.BitcoinPriceDto;

public interface BitcoinService {
    BitcoinPriceDto getCurrentPrice() throws Exception;
}
