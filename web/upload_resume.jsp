<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="css/upload_resume.css">

    <title>Upload Resume | SmartHireLens</title>
</head>
<body>
    <h2>Upload Resume for Matching</h2>
    <form action="UploadResumeServlet" method="post" enctype="multipart/form-data">
        <label>Select Resume (PDF only):</label>
        <input type="file" name="resume" accept=".pdf" required>
        <br><br>
        <input type="submit" value="Upload Resume">
    </form>
</body>
</html>
