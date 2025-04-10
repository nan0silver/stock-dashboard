package org.example.stockdashboard.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.stockdashboard.util.DotenvMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig implements DotenvMixin {

    @Bean
    public DataSource dataSource() {
        try {
            // 드라이버 명시적 등록
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL"));
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASSWORD"));

            // 기본 연결 풀 설정
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);

            return new HikariDataSource(config);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }
}