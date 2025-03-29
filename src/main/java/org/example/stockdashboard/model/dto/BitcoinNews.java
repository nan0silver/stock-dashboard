package org.example.stockdashboard.model.dto;

import java.time.LocalDateTime;

public record BitcoinNews(
        long id,
        String title,
        String url,
        String source,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {
}
