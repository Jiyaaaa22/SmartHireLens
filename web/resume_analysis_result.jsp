<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<html>
<head>
    <title>Resume Analysis Result</title>
    <link rel="stylesheet" href="css/resume_result.css">
</head>
<body>
<div class="container">
    <h1>Resume Analysis Result</h1>

    <div class="score-box">
        <h2>✅ Overall Resume Score: 
            <span class="score">
                <%= request.getAttribute("overallScore") %> / 100
            </span>
        </h2>
    </div>

    <hr>

    <h3>Top Matching Job Descriptions For Your Resume</h3>
    <div class="jd-container">
        <%
            List<Map<String, Object>> matchedJDs = (List<Map<String, Object>>) request.getAttribute("matchedJDs");
            List<Map<String, Object>> matches = (List<Map<String, Object>>) request.getAttribute("matches");

            if (matchedJDs != null && matches != null && !matchedJDs.isEmpty()) {
                for (int i = 0; i < matchedJDs.size(); i++) {
                    Map<String, Object> jd = matchedJDs.get(i);
                    Map<String, Object> match = matches.get(i);
        %>
        <div class="job-card">
            <h2><%= jd.get("title") %> at <%= jd.get("company") %></h2>
            <p><strong>Location:</strong> <%= jd.get("location") %></p>
            <p><strong>Experience:</strong> <%= jd.get("experience") %></p>
            <p><strong>Salary:</strong> <%= jd.get("salary") %></p>
            <p><strong>Job Type:</strong> <%= jd.get("job_type") %></p>
            <p><strong>Key Skills:</strong> <%= jd.get("skills") %></p>
            <!-- <p><strong>Match Score:</strong> <%= String.format("%.2f", match.get("score")) %>%</p>
            <p><strong>Matched Skills:</strong> <%= match.get("matched_skills") %></p> -->
            <p><strong>Description:</strong><br><%= jd.get("description") %></p>

            <!-- Updated Apply Button -->
            <a href="resume_apply.jsp?jobId=<%= jd.get("id") %>">
                <button class="apply-btn">Apply</button>
            </a>
        </div>
        <%  }} else { %>
        <p class="no-matches">No matched job descriptions found.</p>
        <% } %>
    </div>

    <br>
    <a href="candidate/candidate_dashboard.html">
        <button class="back-btn">🔙 Back to Dashboard</button>
    </a>
</div>
</body>
</html>
