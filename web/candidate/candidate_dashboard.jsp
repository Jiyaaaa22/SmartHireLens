<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
     response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies
    String candidateEmail = (String) session.getAttribute("candidateEmail");
     String candidateName = (String) session.getAttribute("candidateName");
     if (candidateEmail == null || candidateName == null) {
        response.sendRedirect("candidate_login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Candidate Dashboard | SmartHireLens</title>
    <link rel="stylesheet" href="../css/candidate_dashboard.css">
</head>
<body>
    <div class="dashboard-container">
        <h1>👋 Welcome to SmartHireLens, <%= candidateName %>!</h1>

        <div id="feedback-msg" style="display:none; padding: 15px; margin-bottom: 20px; text-align: center; border-radius: 8px;"></div>

        <div class="card-container">
            <div class="card">
                <h2> Upload Resume To Get Scored And Matched</h2>
                <p>Upload resume (PDF ONLY) → get score → view matching jobs.</p>
                <a href="../upload_resume.jsp" class="btn">Upload Resume</a>
            </div>

            <div class="card">
                <h2> Applied Jobs</h2>
                <p>View jobs you applied to + uploaded resume.</p>
                <a href="../applied_jobs.jsp" class="btn">View Applications</a>
            </div>

            <div class="card">
                <h2> Logout</h2>
                <p>Logout and return to homepage.</p>
                <a href="../LogoutServlet" class="btn logout">Logout</a>
            </div>
        </div>
    </div>

    <script>
        const urlParams = new URLSearchParams(window.location.search);
        const message = urlParams.get('msg');
        const isError = urlParams.get('error');

        if (message) {
            const feedbackBox = document.getElementById('feedback-msg');
            feedbackBox.innerText = decodeURIComponent(message);
            feedbackBox.style.display = 'block';

            if (isError === 'true') {
                feedbackBox.style.backgroundColor = '#f8d7da';
                feedbackBox.style.color = '#721c24';
                feedbackBox.style.border = '1px solid #f5c6cb';
            } else {
                feedbackBox.style.backgroundColor = '#d4edda';
                feedbackBox.style.color = '#155724';
                feedbackBox.style.border = '1px solid #c3e6cb';
            }
        }

        // prevent back navigation after logout
        if (window.history && window.history.pushState) {
            window.history.pushState(null, null, window.location.href);
            window.onpopstate = function () {
                window.location.href = 'index.jsp';
            };
        }
    </script>
</body>
</html>
