package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.model.dto.BitcoinPrice;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.model.repository.BitcoinRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class BitcoinServiceImpl implements BitcoinService{

    private final BitcoinRepository bitcoinRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BitcoinServiceImpl(BitcoinRepository bitcoinRepository) {
        this.bitcoinRepository = bitcoinRepository;
    }

    @Override
    public BitcoinPriceDto getCurrentPrice() throws Exception {

        BitcoinPrice lastestPrice = bitcoinRepository.getLastestPrice();
        if (lastestPrice != null && lastestPrice.timestamp().isAfter(LocalDateTime.now().minusMinutes(5))) {
            return lastestPrice.toDto();
        }


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

        BitcoinPrice bitcoinPrice = BitcoinPrice.fromDto(BitcoinPriceDto.of(price, priceChange24h));
        bitcoinRepository.savePrice(bitcoinPrice);
        return bitcoinPrice.toDto();
    }

    public List<BitcoinPriceDto> getPriceHistory(int limit) throws Exception {
        List<BitcoinPrice> prices = bitcoinRepository.getPriceHistory(limit);
        return prices.stream().map(BitcoinPrice::toDto).collect(Collectors.toList());
    }

}
