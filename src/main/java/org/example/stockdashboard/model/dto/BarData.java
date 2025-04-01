package org.example.stockdashboard.model.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record BarData(
        long timestamp,
        double open,
        double high,
        double low,
        double close,
        double volume
) {
    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp / 1000),
                ZoneId.systemDefault()
        );
    }
}
