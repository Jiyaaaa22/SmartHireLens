<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String candidateEmail = (String) session.getAttribute("candidateEmail");
    String jobId = request.getParameter("jobId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Upload Resume to Apply | SmartHireLens</title>
    <link rel="stylesheet" href="css/resume_apply.css">
</head>
<body>
<div class="upload-container">
    <h1>📄 Upload Your Resume to Apply</h1>
    <p>Only PDF format is allowed.</p>

    <form action="ApplyResumeServlet" method="post" enctype="multipart/form-data">
        <input type="file" name="resumeFile" accept=".pdf" required>

        <!-- Hidden fields -->
        <input type="hidden" name="candidateEmail" value="<%= candidateEmail %>">
        <input type="hidden" name="jobId" value="<%= jobId %>">

        <button type="submit">Submit Resume</button>
    </form>
</div>
</body>
</html>
