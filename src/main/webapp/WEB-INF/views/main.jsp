<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta property="og:title" content="Bitcoin Insights" />
  <meta
          property="og:description"
          content="Understand Bitcoin and Cryptocurrency Markets"
  />
  <meta
          property="og:image"
          content="https://github.com/nan0silver/stock-dashboard/blob/main/src/main/webapp/assets/bitcoin_dashboard_OG_image.png?raw=true"
  />
  <title>Bitcoin Insights</title>
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">

  <link href="<%= request.getContextPath() %>/assets/main_style.css" rel="stylesheet">
</head>
<body>
<header>
  <div class="container">
    <nav class="flex justify-between items-center mb-8">
      <div class="logo-container">
        <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M11.5 6.5V4.5H12.5V6.5H14V4.5H15V6.5H16V4.5H17V6.5H18V8.5H16.5C17.8807 8.5 19 9.61929 19 11V16C19 17.6569 17.6569 19 16 19H8C6.34315 19 5 17.6569 5 16V11C5 9.61929 6.11929 8.5 7.5 8.5H6V6.5H7V4.5H8V6.5H9V4.5H10V6.5H11.5ZM7.5 10.5C7.22386 10.5 7 10.7239 7 11V16C7 16.5523 7.44772 17 8 17H16C16.5523 17 17 16.5523 17 16V11C17 10.7239 16.7761 10.5 16.5 10.5H7.5ZM10 14H14V15H10V14ZM9 12H15V13H9V12Z" fill="currentColor"/>
        </svg>
        <h1 style="font-size: 1.5rem; font-weight: 700;">Bitcoin Insights</h1>
      </div>
      <div class="nav-links space-x-4">
        <a href="#introduction">Introduction</a>
        <a href="#questions">Market Outlook</a>
        <a href="#price">Live Price</a>
        <a href="#resources">Resources</a>
      </div>
      <button class="subscribe-btn">
        Subscribe for Updates
      </button>
    </nav>

    <div class="hero-content">
      <div class="hero-text">
        <h2 class="hero-title">Understand Bitcoin and Cryptocurrency Markets</h2>
        <p class="hero-subtitle">Real-time insights, market analysis, and comprehensive information about the world's leading cryptocurrency</p>
        <div class="hero-btns flex space-x-4">
          <button class="primary-btn">Live Bitcoin Price</button>
          <button class="secondary-btn">Learn Bitcoin Basics</button>
        </div>
      </div>
      <div class="hero-image">
        <div class="bitcoin-icon">
          <div class="bitcoin-icon-bg"></div>
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9.5 9.5C9.5 8.11929 10.6193 7 12 7H13.5C14.8807 7 16 8.11929 16 9.5C16 10.8807 14.8807 12 13.5 12H12.5H14C15.3807 12 16.5 13.1193 16.5 14.5C16.5 15.8807 15.3807 17 14 17H12C10.6193 17 9.5 15.8807 9.5 14.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 7V5.5M12 17V18.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
    </div>
  </div>

  <div class="stats-bar">
    <div class="container">
      <div class="stats-container">
        <div class="flex items-center space-x-2">
          <span class="stat-label">BTC Price:</span>
          <span class="stat-value">$46,823.45</span>
          <span class="positive-change">+2.34%</span>
        </div>
        <div class="flex items-center space-x-2">
          <span class="stat-label">24h Volume:</span>
          <span class="stat-value">$32.8B</span>
        </div>
        <div class="flex items-center space-x-2">
          <span class="stat-label">Market Cap:</span>
          <span class="stat-value">$891.2B</span>
        </div>
        <div class="flex items-center space-x-2" style="display: none;">
          <span class="material-symbols-outlined">trending_up</span>
          <span class="stat-label">Updated 2 minutes ago</span>
        </div>
      </div>
    </div>
  </div>
</header>

<section id="introduction" class="section" style="background-color: white;">
  <div class="container">
    <div class="intro-section">
      <div class="intro-text">
        <h2 class="section-title">Introduction to Bitcoin</h2>
        <p class="section-content">Bitcoin is a decentralized digital currency created in 2009 by an unknown person or group of people using the name Satoshi Nakamoto. It operates without a central authority or banks, with transaction management and issuance of bitcoins carried out collectively by the network.</p>

        <div class="mb-8">
          <h3 class="feature-title">Key Features</h3>
          <ul class="feature-list" style="list-style: none;">
            <li>
              <span class="material-symbols-outlined feature-icon">check_circle</span>
              <span><strong>Decentralized:</strong> No single entity controls Bitcoin</span>
            </li>
            <li>
              <span class="material-symbols-outlined feature-icon">check_circle</span>
              <span><strong>Limited Supply:</strong> Only 21 million bitcoins will ever exist</span>
            </li>
            <li>
              <span class="material-symbols-outlined feature-icon">check_circle</span>
              <span><strong>Transparent:</strong> All transactions are publicly recorded on the blockchain</span>
            </li>
            <li>
              <span class="material-symbols-outlined feature-icon">check_circle</span>
              <span><strong>Secure:</strong> Protected by cryptography and network consensus</span>
            </li>
          </ul>
        </div>

        <details class="history-dropdown">
          <summary>
            <span>The History of Bitcoin</span>
            <span class="material-symbols-outlined">expand_more</span>
          </summary>
          <div style="padding-top: 1rem;">
            <p>Bitcoin was introduced on October 31, 2008, with the publication of the Bitcoin whitepaper titled "Bitcoin: A Peer-to-Peer Electronic Cash System" by Satoshi Nakamoto. On January 3, 2009, the first block was mined, and the Bitcoin network was born. The true identity of Satoshi Nakamoto remains unknown to this day.</p>
          </div>
        </details>
      </div>

      <div class="intro-card">
        <div class="card">
          <h3 class="feature-title mb-6">How Bitcoin Works</h3>

          <div class="timeline">
            <div class="timeline-line"></div>

            <div class="timeline-item">
              <div class="timeline-number">1</div>
              <h4 style="font-size: 1.125rem; font-weight: 500; margin-bottom: 0.5rem;">Transactions</h4>
              <p style="color: #4b5563;">Users send and receive bitcoins using wallet software. Transactions are broadcast to the network and confirmed through a process called mining.</p>
            </div>

            <div class="timeline-item">
              <div class="timeline-number">2</div>
              <h4 style="font-size: 1.125rem; font-weight: 500; margin-bottom: 0.5rem;">Blockchain</h4>
              <p style="color: #4b5563;">All confirmed transactions are included in the blockchain, a distributed public ledger that prevents double-spending and maintains the integrity of the network.</p>
            </div>

            <div class="timeline-item">
              <div class="timeline-number">3</div>
              <h4 style="font-size: 1.125rem; font-weight: 500; margin-bottom: 0.5rem;">Mining</h4>
              <p style="color: #4b5563;">Mining is the process where specialized computers solve complex mathematical problems to validate and record transactions, receiving new bitcoins as a reward.</p>
            </div>

            <div class="timeline-item" style="margin-bottom: 0;">
              <div class="timeline-number">4</div>
              <h4 style="font-size: 1.125rem; font-weight: 500; margin-bottom: 0.5rem;">Wallets</h4>
              <p style="color: #4b5563;">Bitcoin wallets store the private keys necessary to access and manage your bitcoin holdings, allowing you to send and receive funds.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<section id="questions" class="section" style="background-color: white;">
  <div class="container">
    <h2 class="section-title">비트코인 질문하기</h2>
    <p class="section-content">비트코인이나 암호화폐에 관한 궁금한 점이 있으신가요? AI 전문가에게 질문해보세요.</p>

    <!-- Question Form -->
    <div class="card" style="max-width: 700px; margin: 0 auto; padding: 2rem; border-radius: 1rem; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
      <h3 style="margin-bottom: 1.5rem; text-align: center; font-size: 1.25rem; font-weight: 600;">궁금한 점을 물어보세요</h3>
      <% if (session.getAttribute("message") != null) { %>
      <p><%= session.getAttribute("message") %></p>
      <% } %>
      <form id="questionForm" method="post">
        <div style="margin-bottom: 1rem;">
          <input type="text" name="question" style="width: 100%; padding: 0.75rem; border-radius: 0.5rem; border: 1px solid #e5e7eb; font-size: 1rem;" id="questionInput" placeholder="예: 비트코인 채굴이란 무엇인가요?" required>
        </div>
        <div style="text-align: center;">
          <button type="submit" style="background-image: linear-gradient(to right, #f97316, #eab308); color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 0.5rem; font-weight: 500; cursor: pointer; transition: all 0.3s;">질문하기</button>
        </div>
      </form>
    </div>

    <!-- 이전 질문 예시 -->
    <div style="max-width: 700px; margin: 2rem auto 0; padding: 1rem;">
      <h4 style="margin-bottom: 1rem; font-size: 1rem; font-weight: 600; color: #4b5563;">자주 묻는 질문:</h4>
      <ul style="list-style: none;">
        <li style="margin-bottom: 0.5rem; padding: 0.5rem; background-color: #f9fafb; border-radius: 0.5rem; cursor: pointer; transition: background-color 0.2s;">비트코인과 이더리움의 차이점은 무엇인가요?</li>
        <li style="margin-bottom: 0.5rem; padding: 0.5rem; background-color: #f9fafb; border-radius: 0.5rem; cursor: pointer; transition: background-color 0.2s;">비트코인 지갑은 어떻게 안전하게 보관하나요?</li>
        <li style="margin-bottom: 0.5rem; padding: 0.5rem; background-color: #f9fafb; border-radius: 0.5rem; cursor: pointer; transition: background-color 0.2s;">비트코인 반감기란 무엇이며 가격에 어떤 영향을 미치나요?</li>
      </ul>
    </div>
  </div>
</section>

<!-- 추가 섹션은 여기에 구현할 수 있습니다 -->
<%--<section id="outlook" class="section" style="background-color: #f9fafb;">--%>
<%--    <div class="container">--%>
<%--        <h2 class="section-title">Market Outlook</h2>--%>
<%--        <p class="section-content">Bitcoin market analysis and future predictions from industry experts. Stay informed about the latest trends and factors affecting Bitcoin's price.</p>--%>
<%--        <!-- 여기에 차트와 전문가 분석 내용 추가 -->--%>
<%--    </div>--%>
<%--</section>--%>

<section id="price" class="section" style="background-color: white;">
  <div class="container">
    <h2 class="section-title">Live Bitcoin Price</h2>
    <p class="section-content">Real-time Bitcoin price data, trading volume, and market comparisons.</p>
    <!-- 여기에 실시간 가격 데이터와 차트 추가 -->
    <div class="chart-container" style="width: 700px; margin: auto;">
      <canvas id="priceChart"></canvas>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
      Promise.all([
        fetch("https://api.coingecko.com/api/v3/coins/dogecoin/market_chart?vs_currency=usd&days=7").then(response => response.json()),
        fetch("https://api.coingecko.com/api/v3/coins/bitcoin/market_chart?vs_currency=usd&days=7").then(response => response.json())
      ])
              .then(([dogeData, btcData]) => {
                const ctx = document.getElementById('priceChart').getContext('2d');

                // 날짜 레이블은 도지코인 데이터에서 가져옵니다
                const labels = dogeData.prices.map(item => new Date(item[0]).toLocaleDateString());

                // 비트코인 가격은 굉장히 높기 때문에, 데이터 스케일 조정을 위해 별도의 Y축을 사용합니다
                new Chart(ctx, {
                  type: 'line',
                  data: {
                    labels: labels,
                    datasets: [
                      {
                        label: "도지코인 가격 (USD)",
                        data: dogeData.prices.map(item => item[1]),
                        borderColor: "red",
                        fill: false,
                        yAxisID: 'y-doge'
                      },
                      {
                        label: "비트코인 가격 (USD)",
                        data: btcData.prices.map(item => item[1]),
                        borderColor: "orange",
                        fill: false,
                        yAxisID: 'y-btc'
                      }
                    ]
                  },
                  options: {
                    scales: {
                      'y-doge': {
                        type: 'linear',
                        display: true,
                        position: 'left',
                        title: {
                          display: true,
                          text: '도지코인 가격 (USD)'
                        }
                      },
                      'y-btc': {
                        type: 'linear',
                        display: true,
                        position: 'right',
                        title: {
                          display: true,
                          text: '비트코인 가격 (USD)'
                        },
                        grid: {
                          drawOnChartArea: false // 그리드 라인 중복 방지
                        }
                      }
                    }
                  }
                });
              });
    </script>
  </div>
</section>

<section id="resources" class="section" style="background-color: #f9fafb;">
  <div class="container">
    <h2 class="section-title">Resources</h2>
    <p class="section-content">Educational resources, glossary of cryptocurrency terms, investment guides, and community links.</p>
    <!-- 여기에 리소스 링크와 카드 추가 -->
  </div>
</section>



<footer style="background-color: #1f2937; color: white; padding: 2rem 0;">
  <div class="container">
    <div style="display: flex; justify-content: space-between; flex-wrap: wrap;">
      <div style="margin-bottom: 1.5rem;">
        <h3 style="font-size: 1.25rem; font-weight: 600; margin-bottom: 1rem;">Bitcoin Insights</h3>
        <p style="max-width: 20rem; opacity: 0.8;">Your trusted source for Bitcoin information, market analysis, and cryptocurrency education.</p>
      </div>

      <div style="margin-bottom: 1.5rem;">
        <h4 style="font-size: 1rem; font-weight: 600; margin-bottom: 1rem;">Quick Links</h4>
        <ul style="list-style: none;">
          <li style="margin-bottom: 0.5rem;"><a href="#introduction" style="color: white; text-decoration: none; opacity: 0.8;">Introduction</a></li>
          <li style="margin-bottom: 0.5rem;"><a href="#outlook" style="color: white; text-decoration: none; opacity: 0.8;">Market Outlook</a></li>
          <li style="margin-bottom: 0.5rem;"><a href="#price" style="color: white; text-decoration: none; opacity: 0.8;">Live Price</a></li>
          <li><a href="#resources" style="color: white; text-decoration: none; opacity: 0.8;">Resources</a></li>
        </ul>
      </div>

      <div style="margin-bottom: 1.5rem;">
        <h4 style="font-size: 1rem; font-weight: 600; margin-bottom: 1rem;">Subscribe</h4>
        <p style="margin-bottom: 1rem; opacity: 0.8;">Get the latest Bitcoin news and updates directly to your inbox.</p>
        <div style="display: flex;">
          <input type="email" placeholder="Your email" style="padding: 0.5rem; border-radius: 0.25rem 0 0 0.25rem; border: none; width: 15rem;">
          <button style="background-color: #f97316; color: white; border: none; padding: 0.5rem 1rem; border-radius: 0 0.25rem 0.25rem 0; cursor: pointer;">Subscribe</button>
        </div>
      </div>
    </div>

    <div style="border-top: 1px solid rgba(255, 255, 255, 0.1); padding-top: 1.5rem; margin-top: 1.5rem; text-align: center; opacity: 0.6;">
      <p>&copy; 2025 Bitcoin Insights. All rights reserved.</p>
    </div>
  </div>
</footer>

<script>
  // 여기에 필요한 자바스크립트 코드를 추가할 수 있습니다
  document.addEventListener('DOMContentLoaded', function() {
    // 예: 미디어 쿼리에 따른 모바일 메뉴 토글 기능 등
  });
</script>
</body>
</html>