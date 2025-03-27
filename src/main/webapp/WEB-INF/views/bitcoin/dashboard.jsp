<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>비트코인 대시보드</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    .price-card {
      background-color: #f5f5f5;
      border-radius: 8px;
      padding: 20px;
      max-width: 400px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .current-price {
      font-size: 32px;
      font-weight: bold;
      margin: 10px 0;
    }
    .price-change {
      font-size: 18px;
      margin: 10px 0;
    }
    .positive { color: green; }
    .negative { color: red; }
  </style>
</head>
<body>
<h1>비트코인 대시보드</h1>

<div class="price-card">
  <h2>현재 비트코인 가격</h2>
  <div class="current-price">$${currentPrice.price()}</div>
  <div class="price-change ${currentPrice.priceChange24h().doubleValue() >= 0 ? 'positive' : 'negative'}">
    ${currentPrice.priceChange24h().doubleValue() >= 0 ? '+' : ''}${currentPrice.priceChange24h()}%
  </div>
  <div>마지막 업데이트: ${currentPrice.timestamp()}</div>
</div>

<p><a href="/">메인으로 돌아가기</a></p>
</body>
</html>