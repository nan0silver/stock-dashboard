package org.example.stockdashboard.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BitcoinPriceDto(
        BigDecimal price,
        BigDecimal priceChange24h,
        LocalDateTime timestamp
) {
    public static BitcoinPriceDto of(BigDecimal price, BigDecimal priceChange24h){
        return new BitcoinPriceDto(price, priceChange24h, LocalDateTime.now());
    }
}
