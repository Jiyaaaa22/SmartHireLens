<%@ page import="java.sql.*, java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // --- Get recruiter email from session ---
    String recruiterEmail = (String) session.getAttribute("recruiterEmail");
    if (recruiterEmail == null) {
        response.sendRedirect("recruiter_login.jsp"); // If not logged in
        return;
    }

    // --- JDBC Connection ---
    String url = "jdbc:mysql://localhost:3306/smarthirelens";
    String user = "root";
    String password = "BrightFuture1234##";
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
%>

<html>
<head>
    <title>Your Uploaded Jobs</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }

        table {
            width: 90%;
            margin: 0 auto;
            border-collapse: collapse;
            background: white;
            box-shadow: 0 0 10px #ccc;
        }

        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background: #3f51b5;
            color: white;
        }

        .analyze-btn {
            background-color: #4caf50;
            color: white;
            border: none;
            padding: 8px 12px;
            cursor: pointer;
            border-radius: 5px;
        }

        .analyze-btn:hover {
            background-color: #388e3c;
        }
    </style>
</head>
<body>

<h2 style="text-align: center;">Your Uploaded Job Descriptions</h2>

<%
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url, user, password);

        // --- Get jobs uploaded by this recruiter ---
        String sql = "SELECT * FROM job_descriptions WHERE recruiter_email = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, recruiterEmail);
        rs = stmt.executeQuery();
%>

<table>
    <tr>
        <th>Job Title</th>
        <th>Company</th>
        <th>Posted On</th>
        <th>Action</th>
    </tr>

    <%
        while (rs.next()) {
            int jobId = rs.getInt("id");
            String title = rs.getString("title");
            String company = rs.getString("company_name");
            Timestamp postedDate = rs.getTimestamp("uploaded_on");
    %>

    <tr>
        <td><%= title %></td>
        <td><%= company %></td>
        <td><%= postedDate %></td>
        <td>
            
            <form action="TopCandidatesServlet" method="get" onsubmit="return confirm('Analyze candidates for this job?');">
                <input type="hidden" name="job_id" value="<%= jobId %>">
                <input type="submit" value="Analyze Candidates" class="analyze-btn">
            </form>
        </td>
    </tr>

    <% } %>
</table>

<%
    } catch (Exception e) {
        out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        e.printStackTrace();
    } finally {
        if (rs != null) try { rs.close(); } catch (Exception ignore) {}
        if (stmt != null) try { stmt.close(); } catch (Exception ignore) {}
        if (conn != null) try { conn.close(); } catch (Exception ignore) {}
    }
%>

</body>
</html>
