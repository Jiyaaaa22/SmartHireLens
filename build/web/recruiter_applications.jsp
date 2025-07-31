<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Applied Resumes</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f7ff;
            padding: 30px;
        }
        table {
            width: 80%;
            margin: auto;
            border-collapse: collapse;
            background: #ffffff;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 12px 20px;
            text-align: center;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #3949ab;
            color: white;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        a.download-btn {
            background-color: #4caf50;
            color: white;
            padding: 8px 12px;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
        }
        a.download-btn:hover {
            background-color: #388e3c;
        }
    </style>
</head>
<body>
<h2 style="text-align:center; color:#333;">Applied Resumes</h2>
<%
    String dbURL = "jdbc:mysql://localhost:3306/smarthirelens";
    String dbUser = "root";
    String dbPass = "BrightFuture1234##";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

        String sql = "SELECT ar.id AS resume_id, ar.candidate_email, jd.title, ar.file_name " +
                     "FROM applied_resumes ar " +
                     "JOIN job_descriptions jd ON ar.job_id = jd.id";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
%>
<table>
    <tr>
        <th>Candidate Email</th>
        <th>Job Title</th>
        <th>Resume File</th>
        <th>Action</th>
    </tr>
    <%
        while (rs.next()) {
            int resumeId = rs.getInt("resume_id");
            String email = rs.getString("candidate_email");
            String jobTitle = rs.getString("title");
            String fileName = rs.getString("file_name");
    %>
    <tr>
        <td><%= email %></td>
        <td><%= jobTitle %></td>
        <td><%= fileName %></td>
        <td>
            <a class="download-btn" href="DownloadResumeServlet?resume_id=<%= resumeId %>">Download</a>
        </td>
    </tr>
    <% } %>
</table>
<%
        rs.close();
        ps.close();
        conn.close();
    } catch (Exception e) {
        out.println("<p style='color:red;text-align:center;'>Error: " + e.getMessage() + "</p>");
    }
%>
</body>
</html>
