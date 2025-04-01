package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPrice;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.model.repository.BitcoinRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Override
    public List<BitcoinNews> getLatestNews(int limit) throws Exception {
        List<BitcoinNews> news = bitcoinRepository.getLatestNews(limit);
        if (!news.isEmpty()){
            return news;
        }
        String urlString = "https://min-api.cryptocompare.com/data/v2/news/?lang=EN&categories=BTC";
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

        JsonNode rootNode = objectMapper.readTree(response.toString());
        JsonNode newsArray = rootNode.get("Data");


        for (JsonNode newsItem : newsArray) {
            String title = newsItem.get("title").asText();
            String translatedTitle = transalteToKorean(title);

            String newsUrl = newsItem.get("url").asText();
            String source = newsItem.get("source").asText();
            long publishedEpoch = newsItem.get("published_on").asLong();
            LocalDateTime publishedAt = Instant.ofEpochSecond(publishedEpoch)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            System.out.println(title + " " + newsUrl + " " + publishedAt);

            BitcoinNews bitcoinNews = new BitcoinNews(
                    0,
                    translatedTitle,
                    newsUrl,
                    source,
                    publishedAt,
                    LocalDateTime.now()
            );
            bitcoinRepository.saveNews(bitcoinNews);

        }
        return bitcoinRepository.getLatestNews(limit);
    }

    private String transalteToKorean(String text) throws Exception {
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage("ko")
        );
        return translation.getTranslatedText();
    }

}
