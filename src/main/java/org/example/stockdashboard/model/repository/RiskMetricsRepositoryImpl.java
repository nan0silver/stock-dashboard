package org.example.stockdashboard.model.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Repository
public class RiskMetricsRepositoryImpl implements RiskMetricsRepository {

    private final DataSource dataSource;

    public RiskMetricsRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveRiskMetrics(Map<String, Object> metrics) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO risk_metrics (fear_greed_index, market_sentiment, volatility_30d, price_change_risk, market_health, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, (Integer) metrics.get("fearGreedIndex"));
            pstmt.setString(2, (String) metrics.get("marketSentiment"));
            pstmt.setDouble(3, (Double) metrics.get("volatility30d"));
            pstmt.setString(4, (String) metrics.get("priceChangeRisk"));
            pstmt.setString(5, (String) metrics.get("marketHealth"));
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<String, Object> getLatestRiskMetrics() throws Exception {
        Map<String, Object> metrics = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM risk_metrics ORDER BY timestamp DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                metrics.put("fearGreedIndex", rs.getInt("fear_greed_index"));
                metrics.put("marketSentiment", rs.getString("market_sentiment"));
                metrics.put("volatility30d", rs.getDouble("volatility_30d"));
                metrics.put("priceChangeRisk", rs.getString("price_change_risk"));
                metrics.put("marketHealth", rs.getString("market_health"));
                metrics.put("timestamp", rs.getTimestamp("timestamp").toLocalDateTime());
            }
        }

        return metrics;
    }
}
