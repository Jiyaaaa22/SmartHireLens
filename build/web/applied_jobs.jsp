<%@ page import="java.sql.*" %>
<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*, javax.servlet.http.*, javax.servlet.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String candidateEmail = (String) session.getAttribute("candidateEmail");
    if (candidateEmail == null) {
        response.sendRedirect("candidate/candidate_login.html");
        return;
    }

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

       String sql = "SELECT j.id AS job_id, j.title, j.company_name, j.location, j.experience_required, " +
             "j.salary_offered, j.job_type, j.key_skills, j.description, " +
             "a.id AS resume_id, a.file_name, a.upload_time " +
             "FROM applied_resumes a " +
             "JOIN job_descriptions j ON a.job_id = j.id " +
             "WHERE a.candidate_email = ? ORDER BY a.upload_time DESC";

        ps = conn.prepareStatement(sql);
        ps.setString(1, candidateEmail);
        rs = ps.executeQuery();
%>

<html>
<head>
    <title>My Applied Jobs | SmartHireLens</title>
    <link rel="stylesheet" href="css/applied_jobs.css">
</head>
<body>
    <h1>📝 Jobs You Applied To</h1>
    <div class="job-list">
    <%
        boolean hasData = false;
        while (rs.next()) {
            hasData = true;
    %>
        <div class="job-card">
            <h2><%= rs.getString("title") %></h2>
            <p><strong>Company:</strong> <%= rs.getString("company_name") %></p>
            <p><strong>Location:</strong> <%= rs.getString("location") %></p>
            <p><strong>Experience:</strong> <%= rs.getString("experience_required") %></p>
            <p><strong>Salary:</strong> <%= rs.getString("salary_offered") %></p>
            <p><strong>Job Type:</strong> <%= rs.getString("job_type") %></p>
            <p><strong>Skills:</strong> <%= rs.getString("key_skills") %></p>
            <p><strong>Resume:</strong> <%= rs.getString("file_name") %> 
             <a href="DownloadResumeServlet?resume_id=<%= rs.getInt("resume_id") %>">Download</a>


            </p>
            <p class="time">Applied on: <%= rs.getTimestamp("upload_time") %></p>
        </div>
    <% } %>
    <% if (!hasData) { %>
        <p>You haven’t applied to any jobs yet.</p>
    <% } %>
    </div>
</body>
</html>

<%
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (rs != null) rs.close();
        if (ps != null) ps.close();
        if (conn != null) conn.close();
    }
%>
