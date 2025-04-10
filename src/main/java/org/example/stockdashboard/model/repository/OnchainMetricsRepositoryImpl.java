package org.example.stockdashboard.model.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class OnchainMetricsRepositoryImpl implements OnchainMetricsRepository {

    private final DataSource dataSource;

    public OnchainMetricsRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveOnchainMetrics(Map<String, Object> metrics) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO onchain_metrics (active_wallets, avg_transaction_fee, mining_difficulty, hash_rate, timestamp) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, (Integer) metrics.get("activeWallets"));
            pstmt.setDouble(2, (Double) metrics.get("avgTransactionFee"));
            pstmt.setString(3, (String) metrics.get("miningDifficulty"));
            pstmt.setString(4, (String) metrics.get("hashRate"));
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<String, Object> getLatestOnchainMetrics() throws Exception {
        Map<String, Object> metrics = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM onchain_metrics ORDER BY timestamp DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                metrics.put("activeWallets", rs.getInt("active_wallets"));
                metrics.put("avgTransactionFee", rs.getDouble("avg_transaction_fee"));
                metrics.put("miningDifficulty", rs.getString("mining_difficulty"));
                metrics.put("hashRate", rs.getString("hash_rate"));
                metrics.put("timestamp", rs.getTimestamp("timestamp").toLocalDateTime());
            }
        }

        return metrics;
    }
}