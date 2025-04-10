<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <title>비트코인 대시보드</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon/favicon-96x96.png" sizes="96x96" />
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/assets/favicon/favicon.svg" />
  <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/favicon/favicon.ico" />
  <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/favicon/apple-touch-icon.png" />
  <meta name="apple-mobile-web-app-title" content="BitcoinDashbord" />
  <link rel="manifest" href="${pageContext.request.contextPath}/assets/favicon/site.webmanifest" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/lucide@latest"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
    body {
      font-family: 'Noto Sans KR', sans-serif;
    }
  </style>
</head>
<body class="min-h-screen bg-gray-100 p-4">
<div class="max-w-6xl mx-auto">
  <!-- 헤더 -->
  <div class="flex justify-between items-center mb-6">
    <h1 class="text-3xl font-bold text-gray-800">비트코인 정보 대시보드</h1>
    <div class="flex space-x-2">
      <button class="px-4 py-2 rounded bg-gray-200" onclick="changeTimeRange('1일')">1일</button>
      <button class="px-4 py-2 rounded bg-gray-200" onclick="changeTimeRange('1주')">1주</button>
      <button class="px-4 py-2 rounded bg-gray-200" onclick="changeTimeRange('1개월')">1개월</button>
      <button class="px-4 py-2 rounded bg-blue-600 text-white" onclick="changeTimeRange('1년')">1년</button>
    </div>
  </div>

  <!-- 현재 가격 정보 -->
  <div class="bg-white rounded-lg shadow p-6 mb-6">
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-xl font-semibold text-gray-700 mb-2">현재 비트코인 가격</h2>
        <div class="flex items-end">
          <span class="text-4xl font-bold">$<fmt:formatNumber value="${currentPrice.price()}" pattern="#,##0.00"/></span>
          <span class="ml-2 ${currentPrice.priceChange24h().doubleValue() >= 0 ? 'text-green-500' : 'text-red-500'}">
              ${currentPrice.priceChange24h().doubleValue() >= 0 ? '+' : ''}<fmt:formatNumber value="${currentPrice.priceChange24h()}" pattern="#,##0.00"/>%
            </span>
        </div>
      </div>
      <div class="text-right">
        <p class="text-sm text-gray-500">최종 업데이트</p>
        <p class="text-base">
          <fmt:parseDate value="${currentPrice.timestamp()}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateTime" type="both"/>
          <fmt:formatDate value="${parsedDateTime}" pattern="yyyy년 MM월 dd일 HH:mm"/>
        </p>
      </div>
    </div>
  </div>

  <!-- 메인 컨텐츠 그리드 -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
    <!-- 왼쪽 컬럼: 가격 차트 -->
    <div class="lg:col-span-2 bg-white rounded-lg shadow p-6">
      <h2 class="text-xl font-semibold text-gray-700 mb-4">가격 추이 및 AI 예측</h2>
      <div style="height: 300px;">
        <canvas id="priceChart"></canvas>
      </div>
      <div class="mt-4 p-4 bg-blue-50 rounded-lg">
        <h3 class="font-medium text-blue-800">AI 분석 인사이트</h3>
        <p class="text-sm text-blue-700">
          AI 모델은 현재 추세와 뉴스 감성을 기반으로 단기적으로 상승세가 지속될 것으로 예측합니다.
          고려해야 할 주요 요인: ETF 출시 영향, 기관 투자자 참여 확대, 다가오는 반감기 이벤트.
        </p>
      </div>
    </div>

    <!-- 오른쪽 컬럼: 뉴스 감성 분석 -->
    <div class="bg-white rounded-lg shadow p-6">
      <h2 class="text-xl font-semibold text-gray-700 mb-4">뉴스 감성 분석</h2>
      <div style="height: 200px;">
        <canvas id="sentimentChart"></canvas>
      </div>
      <div class="mt-4">
        <h3 class="font-medium mb-2">최신 뉴스</h3>
        <div class="space-y-3">
          <c:forEach items="${newsWithSentiment}" var="item">
            <div class="flex items-start border-b border-gray-200 pb-2">
              <!-- 감정 분석 결과에 따라 색상 변경 -->
              <c:choose>
                <c:when test="${item.sentiment eq 'POSITIVE'}">
                  <span class="inline-block w-3 h-3 mt-1 mr-2 rounded-full bg-green-500" title="긍정적"></span>
                </c:when>
                <c:when test="${item.sentiment eq 'NEGATIVE'}">
                  <span class="inline-block w-3 h-3 mt-1 mr-2 rounded-full bg-red-500" title="부정적"></span>
                </c:when>
                <c:otherwise>
                  <span class="inline-block w-3 h-3 mt-1 mr-2 rounded-full bg-gray-400" title="중립적"></span>
                </c:otherwise>
              </c:choose>
              <div>
                <h4 class="font-medium text-sm">
                  <a href="${item.news.url()}" target="_blank" class="hover:text-blue-600">${item.news.title()}</a>
                </h4>
                <p class="text-xs text-gray-500">
                  <fmt:parseDate value="${item.news.publishedAt()}" pattern="yyyy-MM-dd'T'HH:mm" var="newsDate" type="both"/>
                  <fmt:formatDate value="${newsDate}" pattern="yyyy-MM-dd"/> | ${item.news.source()}
                  <!-- 감정 분석 결과 텍스트로 표시 -->
                  | <span class="
                <c:choose>
                  <c:when test="${item.sentiment eq 'POSITIVE'}">text-green-600</c:when>
                  <c:when test="${item.sentiment eq 'NEGATIVE'}">text-red-600</c:when>
                  <c:otherwise>text-gray-600</c:otherwise>
                </c:choose>
              ">
                <c:choose>
                  <c:when test="${item.sentiment eq 'POSITIVE'}">긍정적</c:when>
                  <c:when test="${item.sentiment eq 'NEGATIVE'}">부정적</c:when>
                  <c:otherwise>중립적</c:otherwise>
                </c:choose>
              </span>
                </p>
              </div>
            </div>
          </c:forEach>
        </div>
      </div>
    </div>
  </div>

  <!-- 하단 그리드: 추가 분석 -->
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
    <!-- 기술적 지표 -->
    <div class="bg-white rounded-lg shadow p-6">
      <div class="flex items-center mb-4">
        <!--<i data-lucide="trending-up" class="text-blue-600 mr-2"></i>-->
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-600 mr-2">
          <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline>
          <polyline points="17 6 23 6 23 12"></polyline>
        </svg>
        <h2 class="text-lg font-semibold text-gray-700">기술적 지표</h2>
      </div>
      <div class="space-y-2">
        <div class="flex justify-between">
          <span class="text-gray-600">RSI (14)</span>
          <span class="font-medium">${technicalIndicators.rsi}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">MACD (12,26,9)</span>
          <span class="font-medium text-green-600">${technicalIndicators.macd}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">볼린저 밴드</span>
          <span class="font-medium">${technicalIndicators.bollingerBands}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">이동평균 (200일)</span>
          <span class="font-medium text-green-600">${technicalIndicators.movingAverage200d}</span>
        </div>
      </div>
    </div>

    <!-- 온체인 분석 -->
    <div class="bg-white rounded-lg shadow p-6">
      <div class="flex items-center mb-4">
        <!--<i data-lucide="wallet" class="text-blue-600 mr-2"></i>-->
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-600 mr-2">
          <path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"></path>
          <path d="M3 5v14a2 2 0 0 0 2 2h16v-5"></path>
          <path d="M18 12v4"></path>
        </svg>
        <h2 class="text-lg font-semibold text-gray-700">온체인 분석</h2>
      </div>
      <div class="space-y-2">
        <div class="flex justify-between">
          <span class="text-gray-600">월렛 활성화</span>
          <span class="font-medium"><fmt:formatNumber value="${onchainMetrics.activeWallets}" pattern="#,##0"/>+</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">평균 거래 수수료</span>
          <span class="font-medium">$${onchainMetrics.avgTransactionFee}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">채굴 난이도</span>
          <span class="font-medium text-orange-600">${onchainMetrics.miningDifficulty}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">해시레이트</span>
          <span class="font-medium">${onchainMetrics.hashRate}</span>
        </div>
      </div>
    </div>

    <!-- 리스크 지표 -->
    <div class="bg-white rounded-lg shadow p-6">
      <div class="flex items-center mb-4">
        <!--<i data-lucide="alert-triangle" class="text-blue-600 mr-2"></i>-->
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-600 mr-2">
          <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"></path>
          <path d="M12 9v4"></path>
          <path d="M12 17h.01"></path>
        </svg>
        <h2 class="text-lg font-semibold text-gray-700">리스크 지표</h2>
      </div>
      <div class="space-y-2">
        <div class="flex justify-between">
          <span class="text-gray-600">공포 탐욕 지수</span>
          <span class="font-medium text-yellow-600">${riskMetrics.fearGreedIndex} : ${riskMetrics.marketSentiment}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">변동성 (30일)</span>
          <span class="font-medium">${riskMetrics.volatility30d}%</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">급격한 가격 변동 가능성</span>
          <span class="font-medium text-yellow-600">${riskMetrics.priceChangeRisk}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">시장 건전성</span>
          <span class="font-medium text-green-600">${riskMetrics.marketHealth}</span>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  // Lucide 아이콘 초기화
  //lucide.createIcons();

  // 차트 데이터 준비
  const priceHistory = JSON.parse('${priceHistoryJson}');
  console.log("전체 가격 이력 데이터:",priceHistory);
  const predictions = JSON.parse('${predictionDataJson}');
  //const sentimentData = JSON.parse('${sentimentDataJson}');

  // 가격 차트 데이터 준비
  const formatDate = (dateString) => {
    if (!dateString || typeof dateString !== 'string') {
      console.warn("유효하지 않은 날짜 문자열:", dateString);
      return "날짜 오류";
    }

    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
    } catch (e) {
      console.error("날짜 파싱 오류:", e);
      return "날짜 오류";
    }
  };

  // 날짜별 가격 데이터 그룹화
  const groupedPriceHistory = groupByDate(priceHistory);
  console.log("그룹화된 가격 데이터: ", groupedPriceHistory);
  // 그룹화된 가격 차트 데이터 준비
  const priceLabels = groupedPriceHistory.map(item => formatDate(item.timestamp)).reverse();
  const priceValues = groupedPriceHistory.map(item => item.price).reverse();
  console.log("priceHistory END");

  // 예측 차트 데이터 준비
  console.log("predictions START");
  const predictionLabels = predictions.map(item => formatDate(item.date));
  const predictionValues = predictions.map(item => item.predicted);
  const lowerBounds = predictions.map(item => item.lower);
  const upperBounds = predictions.map(item => item.upper);

  // 모든 레이블 병합
  const allLabels = [...priceLabels.slice(-7), ...predictionLabels];

  // 가격 + 예측 차트 그리기
  const priceCtx = document.getElementById('priceChart').getContext('2d');
  new Chart(priceCtx, {
    type: 'line',
    data: {
      labels: allLabels,
      datasets: [
        {
          label: '실제 가격',
          data: [...priceValues.slice(-7), ...Array(predictionLabels.length).fill(null)],
          borderColor: '#2563eb',
          backgroundColor: '#93c5fd',
          fill: false,
          pointRadius: 3
        },
        {
          label: 'AI 예측 가격',
          data: [...Array(priceLabels.slice(-7).length).fill(null), ...predictionValues],
          borderColor: '#7c3aed',
          backgroundColor: 'rgba(196, 181, 253, 0.5)',
          borderDash: [5, 5],
          fill: false
        },
        {
          label: '상한 예측',
          data: [...Array(priceLabels.slice(-7).length).fill(null), ...upperBounds],
          borderColor: 'transparent',
          backgroundColor: 'rgba(196, 181, 253, 0.3)',
          fill: '+1',
          pointRadius: 0
        },
        {
          label: '하한 예측',
          data: [...Array(priceLabels.slice(-7).length).fill(null), ...lowerBounds],
          borderColor: 'transparent',
          backgroundColor: 'rgba(196, 181, 253, 0.3)',
          fill: false,
          pointRadius: 0
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          beginAtZero: false
        }
      }
    }
  });

  // 감정 분석 차트 그리기
  const sentimentCtx = document.getElementById('sentimentChart').getContext('2d');
  const sentimentData = JSON.parse('${sentimentDataJson}');

  // 날짜 형식 변환
  const formattedSentimentData = sentimentData.map(item => ({
    date: formatDate(item.date),
    positive: item.positive,
    negative: item.negative,
    neutral: item.neutral
  }));

  new Chart(sentimentCtx, {
    type: 'bar',
    data: {
      labels: formattedSentimentData.map(item => item.date),
      datasets: [
        {
          label: '긍정적',
          data: formattedSentimentData.map(item => item.positive),
          backgroundColor: '#22c55e'
        },
        {
          label: '부정적',
          data: formattedSentimentData.map(item => item.negative),
          backgroundColor: '#ef4444'
        },
        {
          label: '중립적',
          data: formattedSentimentData.map(item => item.neutral),
          backgroundColor: '#94a3b8'
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          stacked: true
        },
        y: {
          stacked: true
        }
      }
    }
  });

  // 날짜별로 데이터 그룹화하는 함수
  function groupByDate(data) {
    const grouped = {};

    data.forEach(item => {
      const date = new Date(item.timestamp).toISOString().split('T')[0];
      if (!grouped[date]){
        grouped[date] = item;
      }
    });

    return Object.values(grouped);
  }


  // 시간 범위 변경 함수
  function changeTimeRange(range) {
    // 실제 구현에서는 AJAX를 사용하여 서버에서 데이터를 다시 가져와야 합니다
    const buttons = document.querySelectorAll('.px-4.py-2.rounded');
    buttons.forEach(button => {
      if (button.textContent === range) {
        button.classList.add('bg-blue-600', 'text-white');
        button.classList.remove('bg-gray-200');
      } else {
        button.classList.add('bg-gray-200');
        button.classList.remove('bg-blue-600', 'text-white');
      }
    });

    // 알림 표시
    alert(`${range} 데이터를 불러오려면 AJAX 요청이 필요합니다. 실제 구현에서 추가해주세요.`);
  }
</script>
</body>
</html>