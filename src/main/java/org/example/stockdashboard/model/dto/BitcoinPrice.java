package org.example.stockdashboard.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BitcoinPrice(
        long id,
        BigDecimal price,
        BigDecimal priceChange24h,
        LocalDateTime timestamp
) {
    public static BitcoinPrice fromDto(BitcoinPriceDto dto) {
        return new BitcoinPrice(0, dto.price(), dto.priceChange24h(), dto.timestamp());
    }

    public BitcoinPriceDto toDto() {
        return new BitcoinPriceDto(price, priceChange24h, timestamp);
    }
}
