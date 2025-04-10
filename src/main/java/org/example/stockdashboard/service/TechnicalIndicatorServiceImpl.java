package org.example.stockdashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockdashboard.model.dto.BarData;
import org.example.stockdashboard.model.repository.TechnicalRepository;
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
    private final TechnicalRepository technicalRepository;

    public TechnicalIndicatorServiceImpl(RestTemplate restTemplate, TechnicalRepository technicalRepository) {
        this.restTemplate = restTemplate;
        this.technicalRepository = technicalRepository;
    }

    @Override
    public Map<String, Object> fetchAndUpdateTechnicalIndicators() throws Exception {
        String apiKey = dotenv.get("COINCOMPARE_API_KEY");
        String url = "https://min-api.cryptocompare.com/data/v2/histoday?fsym=BTC&tsym=USD&limit=10&api_key=" + apiKey;
        //https://developers.coindesk.com/documentation/legacy/Historical/dataHistoday

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode rootNode = parseJsonResponse(response.getBody());
        JsonNode dataNode = rootNode.get("Data").get("Data");

        List<BarData> priceData = new ArrayList<>();
        List<Double> closePrices = new ArrayList<>();
        double volatility = 4.2;
        for (JsonNode bar : dataNode) {
            priceData.add(new BarData(
                    bar.get("time").asLong() *1000,
                    bar.get("open").asDouble(),
                    bar.get("high").asDouble(),
                    bar.get("low").asDouble(),
                    bar.get("close").asDouble(),
                    bar.get("volumefrom").asDouble()
            ));
            closePrices.add(bar.get("close").asDouble());
        }
        volatility = calculateVolatility(closePrices);

        // ta4j 바 시리즈 생성
        BarSeries series = createBarSeries(priceData);

        // 각 지표 계산
        double rsi = calculateRSI(series);
        String macdSignal = calculateMACDSignal(series);
        String bbSignal = calculateBollingerBandsSignal(series);
        String ma200Signal = calculateMA200Signal(series);

        // 결과 맵 생성 밎 반환
        Map<String, Object> indicators = new HashMap<>();
        indicators.put("rsi", rsi);
        indicators.put("macd", macdSignal);
        indicators.put("bollingerBands", bbSignal);
        indicators.put("movingAverage200d", ma200Signal);
        System.out.println("rsi : "+ rsi);
        System.out.println("movingAverage200d : "+ ma200Signal);

        // 리스크 지표 설정
        indicators.put("volatility30d", Math.round(volatility * 100.0) / 100.0);
        String priceChangeRisk = getPriceChangeRisk(volatility);
        indicators.put("priceChangeRisk", priceChangeRisk);

        technicalRepository.saveTechnicalIndicator(indicators);

        return indicators;
    }

    // DB에서만 데이터를 가져옴
    @Override
    public Map<String, Object> getTechnicalIndicators() throws Exception {
        Map<String, Object> indicators = technicalRepository.getLatestTechnicalIndicator();

        // DB에 데이터가 없는 경우 API를 호출하여 초기화
        if (indicators.isEmpty()) {
            indicators = fetchAndUpdateTechnicalIndicators();
        }

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

    private String calculateBollingerBandsSignal(BarSeries series){
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

    private double calculateVolatility(List<Double> prices) {
        if (prices.size() < 2) {
            return 0.0;
        }

        // 일일 수익률 계산
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double daliyReturn = (prices.get(i) - prices.get(i-1)) / prices.get(i-1) * 100;
            returns.add(daliyReturn);
        }

        double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double variance = returns.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    private String getPriceChangeRisk(double volatility) {
        if (volatility > 5.0) {
            return "높음";
        } else if (volatility > 3.0) {
            return "중간";
        } else {
            return "낮음";
        }
    }
}
