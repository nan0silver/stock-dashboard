package org.example.stockdashboard.model.repository;

import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPrice;
import org.example.stockdashboard.util.DotenvMixin;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
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
    public List<BitcoinPrice> getPriceHistory(int limit) throws Exception {
        List<BitcoinPrice> prices = new ArrayList<>();
        try (Connection conn = getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM bitcoin_prices ORDER BY timestamp DESC LIMIT " + limit;
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

    @Override
    public void saveNews(BitcoinNews news) throws Exception {
        System.out.println("saveNews 메서드 호출 - 제목: " + news.title());

        try (Connection conn = getConnection(url, user, password)) {
            String query = "INSERT INTO bitcoin_news (title, url, source, published_at, created_at) VALUES (?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, news.title());
            pstmt.setString(2, news.url());
            pstmt.setString(3, news.source());
            pstmt.setTimestamp(4, Timestamp.valueOf(news.publishedAt()));
            pstmt.setTimestamp(5, Timestamp.valueOf(news.createdAt()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<BitcoinNews> getLatestNews(int limit) throws Exception {
        List<BitcoinNews> news = new ArrayList<>();
        try (Connection conn = getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM bitcoin_news ORDER BY published_at DESC LIMIT " + limit;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                news.add(new BitcoinNews(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getString("source"),
                        rs.getTimestamp("published_at").toLocalDateTime(),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("sentiment")
                ));
            }
        }
        return news;
    }

    @Override
    public boolean existsByUrl(String url) throws Exception {
        boolean exists = false;
        try(Connection conn = getConnection(this.url, user, password)) {
            String query = "SELECT COUNT(*) FROM bitcoin_news WHERE url = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, url);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                exists = rs.getInt(1) > 0;
            }
        }
        return exists;
    }
}
