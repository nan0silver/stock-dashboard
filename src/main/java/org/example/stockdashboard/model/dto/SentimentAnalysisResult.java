package org.example.stockdashboard.model.dto;

import java.time.LocalDateTime;

public record SentimentAnalysisResult(
        LocalDateTime date,
        int positive,
        int negative,
        int neutral
) {
}
