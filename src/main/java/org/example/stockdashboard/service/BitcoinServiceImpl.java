package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPrice;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.model.dto.SentimentAnalysisResult;
import org.example.stockdashboard.model.repository.BitcoinRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BitcoinServiceImpl implements BitcoinService{

    private final BitcoinRepository bitcoinRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    public BitcoinServiceImpl(BitcoinRepository bitcoinRepository, RestTemplate restTemplate) {
        this.bitcoinRepository = bitcoinRepository;
        this.restTemplate = restTemplate;
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
        // 단순히 DB에서 최신 뉴스를 가져오는 역할
        return bitcoinRepository.getLatestNews(limit);
    }

    @Override
    public void updateNewsFromAPI() throws Exception {
        System.out.println("API에서 뉴스 업데이트 시작: " + LocalDateTime.now());
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

        // 가장 최근에 저장된 뉴스의 시간 가져오기
        List<BitcoinNews> latestSavedNews = bitcoinRepository.getLatestNews(1);
        LocalDateTime latestSavedTime = latestSavedNews.isEmpty() ?
                LocalDateTime.MIN : latestSavedNews.get(0).publishedAt();

        int newNewsCount = 0;
        int newsLimit = 5;


        for (JsonNode newsItem : newsArray) {
            if (newNewsCount >= newsLimit) {
                break;
            }
            String title = newsItem.get("title").asText();
            String translatedTitle = transalteToKorean(title);

            String newsUrl = newsItem.get("url").asText();
            String source = newsItem.get("source").asText();
            long publishedEpoch = newsItem.get("published_on").asLong();
            LocalDateTime publishedAt = Instant.ofEpochSecond(publishedEpoch)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            System.out.println("뉴스 저장 시도: " + title + " " + newsUrl + " " + publishedAt);

            // 이미 저장된 뉴스보다 새로운 뉴스만 저장
            // !bitcoinRepository.existsByUrl(newsUrl) -> 새 뉴스만 저장
            if (publishedAt.isAfter(latestSavedTime) && !bitcoinRepository.existsByUrl(newsUrl)){
                BitcoinNews bitcoinNews = new BitcoinNews(
                        0,
                        translatedTitle,
                        newsUrl,
                        source,
                        publishedAt,
                        LocalDateTime.now()
                );
                try {
                    bitcoinRepository.saveNews(bitcoinNews);
                    newNewsCount++;
                    System.out.println("뉴스 저장 성공");
                } catch (Exception e) {
                    System.err.println("뉴스 저장 실패: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        System.out.println("뉴스 업데이트 완료. 추가된 뉴스: " + newNewsCount + "개");
    }

    @Override
    public List<SentimentAnalysisResult> getNewsSentiment(int days) throws Exception {
        List<SentimentAnalysisResult> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        //최근 뉴스 가져오기
        List<BitcoinNews> allNews = bitcoinRepository.getLatestNews(100);

        Map<LocalDate, List<BitcoinNews>> newsByDate = allNews.stream()
                .collect(Collectors.groupingBy(
                        news -> news.publishedAt().toLocalDate()
                ));

        for (int i = 0; i < days; i++) {
            LocalDate date = now.minusDays(i).toLocalDate();
            List<BitcoinNews> newsOfDay = newsByDate.getOrDefault(date, Collections.emptyList());

            if (newsOfDay.isEmpty()) {
                continue;
            }
            double positive = 0, negative=0, neutral=0;

            // 감정 분석 실행
            for (BitcoinNews news : newsOfDay) {
                String sentiment = analyzeSentiment(news.title());

                if ("POSITIVE".equals(sentiment)) {
                    positive++;
                } else if ("NEGATIVE".equals(sentiment)) {
                    negative++;
                } else {
                    neutral++;
                }
            }

            // 총 뉴스 수에 대한 비율 계산
            int total = newsOfDay.size();
            results.add(new SentimentAnalysisResult(
                    date.atStartOfDay(),
                    (positive / total) * 100,
                    (negative / total) * 100,
                    (neutral / total) * 100
            ));
        }

        // 날짜순으로 정렬 (오래된 날짜부터)
        results.sort(Comparator.comparing(SentimentAnalysisResult::date));

        return results;
    }

    public String analyzeSentiment(String text){
        try {
            // 무료 감정 분석 API 호출 (예: Text Processing API)
            String apiUrl = "https://text-processing.com/api/sentiment/";

            // POST 요청 준비
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("text", text);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            // 결과 해석
            String label = rootNode.get("label").asText();
            if ("pos".equals(label)) {
                return "POSITIVE";
            } else if ("neg".equals(label)) {
                return "NEGATIVE";
            } else {
                return "NEUTRAL";
            }
        } catch (Exception e) {
            // API 호출 실패 시 기본값 반환
            // 단어 기반 간단한 감정 분석 대체
            return simpleWordBasedSentiment(text);
        }
    }

    private String simpleWordBasedSentiment(String text) {
        return "";
    }

    private String transalteToKorean(String text) throws Exception {
        //https://libretranslate.com/
        try {
            String apiUrl = "https://libretranslate.com/translate";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // 파라미터 구성
            String postData = "q=" + URLEncoder.encode(text, "UTF-8")
                    + "&source=en&target=ko";

            // 요청 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // 응답 처리
            if (conn.getResponseCode() == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }

                JsonNode rootNode = objectMapper.readTree(response.toString());
                return rootNode.get("translatedText").asText();
            } else {
                System.err.println("번역 API 호출 실패: " + conn.getResponseCode());
                return text;
            }


        } catch (Exception e) {
            System.err.println("번역 중 오류 발생: " + e.getMessage());
            return text;
        }
    }
}
