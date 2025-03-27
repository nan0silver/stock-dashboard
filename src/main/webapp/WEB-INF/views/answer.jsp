<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bitcoin Insights - 답변</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">

    <link href="<%= request.getContextPath() %>/assets/answer_style.css" rel="stylesheet">

</head>
<body>
<div class="container">
    <header>
        <div class="header-content">
            <div class="logo-container">
                <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M11.5 6.5V4.5H12.5V6.5H14V4.5H15V6.5H16V4.5H17V6.5H18V8.5H16.5C17.8807 8.5 19 9.61929 19 11V16C19 17.6569 17.6569 19 16 19H8C6.34315 19 5 17.6569 5 16V11C5 9.61929 6.11929 8.5 7.5 8.5H6V6.5H7V4.5H8V6.5H9V4.5H10V6.5H11.5ZM7.5 10.5C7.22386 10.5 7 10.7239 7 11V16C7 16.5523 7.44772 17 8 17H16C16.5523 17 17 16.5523 17 16V11C17 10.7239 16.7761 10.5 16.5 10.5H7.5ZM10 14H14V15H10V14ZM9 12H15V13H9V12Z" fill="currentColor"/>
                </svg>
                <h1 style="font-size: 1.5rem; font-weight: 700;">Bitcoin Insights</h1>
            </div>
            <a href="/" class="back-btn">
                <span class="material-symbols-outlined">arrow_back</span>
                <span>돌아가기</span>
            </a>
        </div>
    </header>

    <% if (session.getAttribute("answer") != null) { %>
    <div class="answer-card">
        <div class="question-label">질문</div>
        <div class="question-text"><%= session.getAttribute("question")%></div>

        <div class="answer-content">
            <%= session.getAttribute("answer")%>
        </div>
        <!-- thinking 섹션 - 기본적으로 숨김 처리 -->
        <div class="thinking-section">
            <button id="thinking-toggle" class="toggle-btn">
                <span class="material-symbols-outlined">psychology</span>
                <span class="toggle-text">AI의 사고 과정 보기</span>
            </button>

            <div id="thinking-content" class="answer-content thinking-content" style="display: none;">
                <%= session.getAttribute("thinking")%>
            </div>
        </div>
        <div class="answer-content">
            <%= session.getAttribute("reasoning")%>
        </div>
        <div class="answer-image-container">
            <img alt="<%= session.getAttribute("answer")%>" src="<%= session.getAttribute("image")%>">
        </div>

        <div class="answer-meta">
            <span>마지막 업데이트: <%= new java.text.SimpleDateFormat("yyyy년 MM월 dd일").format(new java.util.Date()) %></span>

            <div class="feedback-btns">
                <button class="feedback-btn">
                    <span class="material-symbols-outlined">thumb_up</span>
                    <span>도움됨</span>
                </button>
                <button class="feedback-btn">
                    <span class="material-symbols-outlined">thumb_down</span>
                    <span>도움안됨</span>
                </button>
            </div>
        </div>
    </div>

    <div class="related-questions">
        <h3 class="related-title">관련 질문</h3>
        <ul class="related-list">
            <li class="related-item">
                <a href="#" class="related-link">
                    <span class="material-symbols-outlined related-icon">help_outline</span>
                    <span>비트코인과 도지코인의 차이점은 무엇인가요?</span>
                </a>
            </li>
            <li class="related-item">
                <a href="#" class="related-link">
                    <span class="material-symbols-outlined related-icon">help_outline</span>
                    <span>도지코인에 투자하는 방법은 무엇인가요?</span>
                </a>
            </li>
            <li class="related-item">
                <a href="#" class="related-link">
                    <span class="material-symbols-outlined related-icon">help_outline</span>
                    <span>암호화폐 지갑은 어떻게 안전하게 보관하나요?</span>
                </a>
            </li>
        </ul>
    </div>
    <% } else { %>
    <div class="answer-card">
        <div class="question-text">질문을 찾을 수 없습니다</div>
        <p>질문을 다시 시도해주세요.</p>
    </div>
    <% } %>

    <!-- 플로팅 액션 버튼 추가 -->
    <div class="floating-actions">
        <button class="floating-btn primary" id="ask-new-question" title="새 질문하기">
            <span class="material-symbols-outlined">add</span>
        </button>
        <button class="floating-btn" id="scroll-top" title="맨 위로 이동">
            <span class="material-symbols-outlined">arrow_upward</span>
        </button>
    </div>
</div>

<script>
    // 피드백 버튼 기능
    document.querySelectorAll('.feedback-btn').forEach(button => {
        button.addEventListener('click', function() {
            // 여기에 피드백을 처리하는 코드를 추가할 수 있습니다
            alert('소중한 피드백 감사합니다!');
        });
    });

    // 관련 질문 링크
    document.querySelectorAll('.related-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            // 여기에 관련 질문을 처리하는 코드를 추가할 수 있습니다
            const question = this.querySelector('span:last-child').textContent;
            window.location.href = 'index.jsp?question=' + encodeURIComponent(question);
        });
    });

    // 사고 과정 토글 기능
    document.addEventListener('DOMContentLoaded', function() {
        const thinkingToggle = document.getElementById('thinking-toggle');
        const thinkingContent = document.getElementById('thinking-content');

        if (thinkingToggle && thinkingContent) {
            thinkingToggle.addEventListener('click', function() {
                // 내용 표시 여부 토글
                if (thinkingContent.style.display === 'none') {
                    thinkingContent.style.display = 'block';
                    thinkingToggle.classList.add('active');
                    thinkingToggle.querySelector('.toggle-text').textContent = 'AI의 사고 과정 숨기기';
                } else {
                    thinkingContent.style.display = 'none';
                    thinkingToggle.classList.remove('active');
                    thinkingToggle.querySelector('.toggle-text').textContent = 'AI의 사고 과정 보기';
                }
            });
        }
    });
</script>
</body>
</html>