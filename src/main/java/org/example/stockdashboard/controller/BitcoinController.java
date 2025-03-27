package org.example.stockdashboard.controller;

import org.example.stockdashboard.model.dto.BitcoinPriceDto;
import org.example.stockdashboard.service.BitcoinService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        return "bitcoin/dashboard";
    }
}

