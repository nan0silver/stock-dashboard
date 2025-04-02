package org.example.stockdashboard.model.dto;

import java.time.LocalDateTime;

public record SentimentAnalysisResult(
        LocalDateTime date,
        double positive,
        double negative,
        double neutral
) {
}
