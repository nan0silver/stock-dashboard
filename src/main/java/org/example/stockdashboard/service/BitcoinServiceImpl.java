package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class BitcoinServiceImpl implements BitcoinService{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BitcoinPriceDto getCurrentPrice() throws Exception {
        String urlString = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd&include_24hr_change=true";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        StringBuilder response = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        //JSON 응답 파싱
        JsonNode rootNode = objectMapper.readTree(response.toString());
        JsonNode bitcoinNode = rootNode.get("bitcoin");

        BigDecimal price = BigDecimal.valueOf(bitcoinNode.get("usd").asDouble());
        BigDecimal priceChange24h = BigDecimal.valueOf(bitcoinNode.get("usd_24h_change").asDouble());

        return BitcoinPriceDto.of(price, priceChange24h);
    }
}
