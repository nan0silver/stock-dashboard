<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>비트코인 대시보드</title>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
    .chart-container {
      background-color: #f5f5f5;
      border-radius: 8px;
      padding: 20px;
      margin-top: 20px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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

<div class="chart-container">
  <h2>비트코인 가격 추이</h2>
  <canvas id="priceChart"></canvas>
</div>

<p><a href="/">메인으로 돌아가기</a></p>


<script>
  // 가격 이력 데이터 가져오기
  const priceHistory = JSON.parse('${priceHistoryJson}');

  // 차트에 표시할 데이터 준비
  const labels = priceHistory.map(item => {
    const date = new Date(item.timestamp);
    return date.toLocaleDateString();
  }).reverse();  // 시간순 정렬

  const prices = priceHistory.map(item => item.price).reverse();

  // 차트 생성
  const ctx = document.getElementById('priceChart').getContext('2d');
  const chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: '비트코인 가격 (USD)',
        data: prices,
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        tension: 0.1,
        fill: true
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: false,
          ticks: {
            callback: function(value) {
              return '$' + value;
            }
          }
        }
      }
    }
  });
</script>


</body>
</html>