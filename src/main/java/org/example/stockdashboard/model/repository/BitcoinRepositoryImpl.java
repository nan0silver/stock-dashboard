package org.example.stockdashboard.model.repository;

import org.example.stockdashboard.model.dto.BitcoinPrice;
import org.example.stockdashboard.util.DotenvMixin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitcoinRepositoryImpl implements BitcoinRepository, DotenvMixin {
    final String url = dotenv.get("DB_URL");
    final String user = dotenv.get("DB_USER");
    final String password = dotenv.get("DB_PASSWORD");

    @Override
    public BitcoinPrice getLastestPrice() throws Exception {
        BitcoinPrice price = null;
        try  (Connection conn  = getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM bitcoin_prices ORDER BY timestamp DESC LIMIT 1";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                price = new BitcoinPrice(
                        rs.getLong("id"),
                        rs.getBigDecimal("price"),
                        rs.getBigDecimal("price_change_24h"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
            }
        }
        return price;
    }

    @Override
    public void savePrice(BitcoinPrice price) throws Exception {
        try (Connection conn = getConnection(url, user, password)) {
            String query = "INSERT INTO bitcoin_prices (price, price_change_24h, timestamp) VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setBigDecimal(1, price.price());
            pstmt.setBigDecimal(2, price.priceChange24h());
            pstmt.setTimestamp(3, Timestamp.valueOf(price.timestamp()));
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<BitcoinPrice> getPriceHistory(int linit) throws Exception {
        List<BitcoinPrice> prices = new ArrayList<>();
        try (Connection conn = getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM bitcoin_prices ORDER BY timestamp DESC LIMIT " + linit;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                prices.add(new BitcoinPrice(
                        rs.getLong("id"),
                        rs.getBigDecimal("price"),
                        rs.getBigDecimal("price_change_24h"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        }
        return prices;
    }
}
