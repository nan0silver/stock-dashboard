package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.model.dto.BarData;
import org.example.stockdashboard.util.DotenvMixin;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TechnicalIndicatorServiceImpl implements TechnicalIndicatorService, DotenvMixin {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TechnicalIndicatorServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> getTechnicalIndicators() throws Exception {
        String apiKey = dotenv.get("COINCOMPARE_API_KEY");
        String url = "https://min-api.cryptocompare.com/data/v2/technical/indicators/RSI,MACD,BB?fsym=BTC&tsym=USD&limit=1&api_key=" + apiKey;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode rootNode = parseJsonResponse(response.getBody());
        JsonNode dataNode = rootNode.get("Data").get("Data");

        List<BarData> priceData = new ArrayList<>();
        for (JsonNode bar : dataNode) {
            priceData.add(new BarData(
                    bar.get("time").asLong() *1000,
                    bar.get("open").asDouble(),
                    bar.get("high").asDouble(),
                    bar.get("low").asDouble(),
                    bar.get("close").asDouble(),
                    bar.get("volumefrom").asDouble()
            ));
        }

        // ta4j 바 시리즈 생성
        BarSeries series = createBarSeries(priceData);

        // 각 지표 계산
        double rsi = calculateRSI(series);
        String macdSignal = calculateMACDSignal(series);
        String bbSignal = calsulateBollingerBandsSignal(series);
        String ma200Signal = calculateMA200Signal(series);

        // 결과 맵 생성 밎 반환
        Map<String, Object> indicators = new HashMap<>();
        indicators.put("rsi", rsi);
        indicators.put("macd", macdSignal);
        indicators.put("bollingerBands", bbSignal);
        indicators.put("movingAverage200d", ma200Signal);

        return indicators;
    }

    private JsonNode parseJsonResponse(String jsonResponse) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(jsonResponse);
        }catch (Exception e){
            throw new RuntimeException("Fail to parse API response", e);
        }
    }

    private BarSeries createBarSeries(List<BarData> priceData){
        BarSeries series = new BaseBarSeries();

        for (BarData bar : priceData) {
            ZonedDateTime time = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(bar.timestamp()),
                    ZoneId.systemDefault()
            );

            series.addBar(
                    time,
                    bar.open(),
                    bar.high(),
                    bar.low(),
                    bar.close(),
                    bar.volume()
            );
        }
        return series;
    }

    private double calculateRSI(BarSeries series) {
        int periodLength = 14;

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePriceIndicator, periodLength);

        // 가장 최근 RSI값 반환
        int lastIndex = series.getBarCount() -1;
        return rsi.getValue(lastIndex).doubleValue();
    }

    private String calculateMACDSignal(BarSeries series) {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

        // 12일, 26일, 9일을 사용한 MACD
        MACDIndicator macdIndicator = new MACDIndicator(closePriceIndicator, 12, 26);
        EMAIndicator emaIndicator = new EMAIndicator(macdIndicator, 9);

        int lastIndex = series.getBarCount()-1;
        double macdValue = macdIndicator.getValue(lastIndex).doubleValue();
        double signalValue = emaIndicator.getValue(lastIndex).doubleValue();

        // MACD 신호 판단
        if (macdValue > signalValue) {
            return "상승";
        }else if (macdValue < signalValue) {
            return "하락";
        }else {
            return "중립";
        }
    }

    private String calsulateBollingerBandsSignal(BarSeries series){
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

        // 20일 기간, 2 표준편차 사용
        int period = 20;
        double stdDevMultiplier = 2.0;

        SMAIndicator smaIndicator = new SMAIndicator(closePriceIndicator, period);
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePriceIndicator, period);

        int lastIndex = series.getBarCount()-1;
        double upperBand = smaIndicator.getValue(lastIndex).doubleValue() +
                (stdDev.getValue(lastIndex).doubleValue() * stdDevMultiplier);
        double middleBand = smaIndicator.getValue(lastIndex).doubleValue();
        double lowerBand = smaIndicator.getValue(lastIndex).doubleValue() -
                (stdDev.getValue(lastIndex).doubleValue() * stdDevMultiplier);
        double lastClose = closePriceIndicator.getValue(lastIndex).doubleValue();

        // 볼린저 밴드 신호 판단
        if (lastClose > upperBand) {
            return "상단 돌파";
        } else if (lastClose > middleBand && lastClose < upperBand) {
            return "상단 접근중";
        } else if (lastClose < lowerBand) {
            return "하단 돌파";
        } else if (lastClose < middleBand && lastClose > lowerBand) {
            return "하단 접근중";
        } else {
            return "중앙대 유지";
        }
    }

    private String calculateMA200Signal(BarSeries series) {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        SMAIndicator sma200 = new SMAIndicator(closePriceIndicator, 200);

        int lastIndex = series.getBarCount()-1;
        double ma200Value = sma200.getValue(lastIndex).doubleValue();
        double lastClose = closePriceIndicator.getValue(lastIndex).doubleValue();

        //MA200 신호 판단
        if (lastClose > ma200Value) {
            return "상회";
        } else if (lastClose < ma200Value) {
            return "하회";
        } else {
            return "일치";
        }
    }

    @Override
    public Map<String, Object> getOnchainMetrics() throws Exception {
        return Map.of();
    }

    @Override
    public Map<String, Object> getRiskMetrics() throws Exception {
        return Map.of();
    }
}
