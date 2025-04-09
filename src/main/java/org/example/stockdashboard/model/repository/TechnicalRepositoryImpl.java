package org.example.stockdashboard.model.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TechnicalRepositoryImpl implements TechnicalRepository {
    private final DataSource dataSource;

    public TechnicalRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveTechnicalIndicator(Map<String, Object> indicators) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO technical_indicators (rsi, macd, bollinger_bands, moving_average_200d, volatility_30d) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setDouble(1, (Double) indicators.get("rsi"));
            pstmt.setString(2, (String) indicators.get("macd"));
            pstmt.setString(3, (String) indicators.get("bollingerBands"));
            pstmt.setString(4, (String) indicators.get("movingAverage200d"));
            pstmt.setDouble(5, (Double) indicators.get("volatility30d"));

            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<String, Object> getLatestTechnicalIndicator() throws Exception {
        Map<String, Object> indicators = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM technical_indicators ORDER BY timestamp DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                indicators.put("rsi", rs.getDouble("rsi"));
                indicators.put("macd", rs.getString("macd"));
                indicators.put("bollingerBands", rs.getString("bollinger_bands"));
                indicators.put("movingAverage200d", rs.getString("moving_average_200d"));
                indicators.put("volatility30d", rs.getDouble("volatility_30d"));
                indicators.put("timestamp", rs.getTimestamp("timestamp").toLocalDateTime());
            }
        }

        return indicators;
    }
}