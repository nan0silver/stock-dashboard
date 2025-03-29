package org.example.stockdashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.stockdashboard.model.dto.BitcoinNews;
import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.service.BitcoinService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/bitcoin")
public class BitcoinController {

    private final BitcoinService bitcoinService;

    public BitcoinController(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    @GetMapping
    public String dashboard(Model model) throws Exception {
        //현재 비트코인 가격 조회
        BitcoinPriceDto currentPrice = bitcoinService.getCurrentPrice();
        model.addAttribute("currentPrice", currentPrice);

        //가격 이력 조회
        List<BitcoinPriceDto> priceHistory = bitcoinService.getPriceHistory(30);
        model.addAttribute("priceHistory", priceHistory);

        //차트 데이터 생성
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String priceHistoryJson = objectMapper.writeValueAsString(priceHistory);
        model.addAttribute("priceHistoryJson", priceHistoryJson);

        //뉴스 데이터 추가
        List<BitcoinNews> latestNews = bitcoinService.getLatestNews(5);
        model.addAttribute("latestNews", latestNews);

        return "bitcoin/dashboard";
    }
}

