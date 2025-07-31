<%@ page import="java.sql.*" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String jobId = request.getParameter("id");
    if (jobId == null || jobId.isEmpty()) {
        out.println("<h3>Invalid Job ID</h3>");
        return;
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String title = "", company = "", location = "", experience = "", salary = "", jobType = "", skills = "", description = "";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

        pstmt = conn.prepareStatement("SELECT * FROM job_descriptions WHERE id = ?");
        pstmt.setInt(1, Integer.parseInt(jobId));
        rs = pstmt.executeQuery();

        if (rs.next()) {
            title = rs.getString("title");
            company = rs.getString("company_name");
            location = rs.getString("location");
            experience = rs.getString("experience_required");
            salary = rs.getString("salary_offered");
            jobType = rs.getString("job_type");
            skills = rs.getString("key_skills");
            description = rs.getString("description");
        } else {
            out.println("<h3>Job Not Found</h3>");
            return;
        }
    } catch (Exception e) {
        out.println("Error: " + e.getMessage());
        return;
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Job Description</title>
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(to right, #d4fcf7, #f9d8ec);
            margin: 0;
            padding: 0;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background-color: #ffffff;
            padding: 40px 50px;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            width: 90%;
            max-width: 650px;
            overflow-y: auto;
            max-height: 90vh;
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #2c3e50;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #34495e;
        }

        input[type="text"],
        textarea,
        select {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
            font-size: 14px;
            background-color: #f8f9fa;
            transition: 0.3s ease;
        }

        input[type="text"]:focus,
        textarea:focus {
            border-color: #4CAF50;
            background-color: #fff;
            outline: none;
        }

        textarea {
            resize: vertical;
            min-height: 100px;
        }

        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            width: 100%;
            transition: background-color 0.3s ease;
        }

        input[type="submit"]:hover {
            background-color: #388e3c;
        }

        @media (max-height: 600px) {
            body {
                align-items: flex-start;
                padding-top: 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Edit Job Description</h2>
        <form action="UpdateJDServlet" method="post">
            <input type="hidden" name="id" value="<%= jobId %>">

            <label>Job Title:</label>
            <input type="text" name="title" value="<%= title %>" required>

            <label>Company Name:</label>
            <input type="text" name="company_name" value="<%= company %>" required>

            <label>Location:</label>
            <input type="text" name="location" value="<%= location %>" required>

            <label>Experience Required:</label>
            <input type="text" name="experience_required" value="<%= experience %>" required>

            <label>Salary Offered:</label>
            <input type="text" name="salary_offered" value="<%= salary %>" required>

            <label>Job Type:</label>
            <input type="text" name="job_type" value="<%= jobType %>" required>

            <label>Key Skills:</label>
            <textarea name="key_skills" required><%= skills %></textarea>

            <label>Job Description:</label>
            <textarea name="description" required><%= description %></textarea>

            <input type="submit" value="Update Job">
        </form>
    </div>
</body>
</html>
