<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*, java.util.*" %>
<%@ page session="true" %>
<%
    String recruiterEmail = (String) session.getAttribute("recruiterEmail");
    if (recruiterEmail == null) {
        response.sendRedirect("recruiter/recruiter_login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Uploaded JDs</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(to right, #e0f7fa, #fce4ec);
            margin: 0;
            padding: 40px;
            color: #1e293b;
        }

        h2 {
            text-align: center;
          color: #2c3e50;
            font-size: 30px;
            margin-bottom: 30px;
            text-shadow: 0 2px 5px rgba(0,0,0,0.2);
        }

        .table-container {
            background-color: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
            padding: 25px;
            max-width: 100%;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 1200px;
            border-radius: 12px;
            overflow: hidden;
        }

        th {
            background: linear-gradient(to right, #6366f1, #3b82f6);
            color: #ffffff;
            padding: 14px;
            font-size: 14px;
            text-align: left;
        }

        td {
            padding: 12px;
            font-size: 14px;
            border-bottom: 1px solid #e2e8f0;
            color: #334155;
            background-color: #f9fafb;
        }

        tr:nth-child(even) td {
            background-color: #f1f5f9;
        }

        tr:hover td {
            background-color: #e0f2fe;
        }

        .button, input[type="submit"] {
            background: linear-gradient(to right, #10b981, #22c55e);
            color: white;
            border: none;
            padding: 8px 14px;
            border-radius: 8px;
            text-decoration: none;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
        }

        .button:hover, input[type="submit"]:hover {
            background: #059669;
            transform: scale(1.05);
        }

        input[type="submit"] {
            width: 100%;
        }

        form {
            margin: 0;
        }

        @media (max-width: 768px) {
            table {
                font-size: 13px;
                min-width: 1000px;
            }

            .table-container {
                padding: 15px;
            }

            h2 {
                font-size: 22px;
            }
        }
    </style>
</head>
<body>
    <h2>📄 Your Uploaded Job Descriptions</h2>
    <div class="table-container">
        <table>
            <tr>
                <th>Title</th>
                <th>Company</th>
                <th>Location</th>
                <th>Experience</th>
                <th>Type</th>
                <th>Salary</th>
                <th>Description</th>
                <th>Skills</th>
                <th>Edit</th>
                <th>Delete</th>
            </tr>
<%
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

        PreparedStatement ps = con.prepareStatement(
            "SELECT id, title, company_name, location, experience_required, job_type, salary_offered, description, Key_skills FROM job_descriptions WHERE recruiter_email = ?");
        ps.setString(1, recruiterEmail);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int jobId = rs.getInt("id");
%>
            <tr>
                <td><%= rs.getString("title") %></td>
                <td><%= rs.getString("company_name") %></td>
                <td><%= rs.getString("location") %></td>
                <td><%= rs.getString("experience_required") %></td>
                <td><%= rs.getString("job_type") %></td>
                <td><%= rs.getString("salary_offered") %></td>
                <td><%= rs.getString("description") %></td>
                <td><%= rs.getString("Key_skills") %></td>
                <td>
                    <a class="button" href="editJDForm.jsp?id=<%= jobId %>">Edit</a>
                </td>
                <td>
                    <form action="DeleteJDServlet" method="post" onsubmit="return confirm('Are you sure you want to delete this JD?');">
                        <input type="hidden" name="jobId" value="<%= jobId %>">
                        <input type="submit" value="Delete">
                    </form>
                </td>
            </tr>
<%
        }
        rs.close();
        con.close();
    } catch (Exception e) {
        out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
    }
%>
        </table>
    </div>
</body>
</html>
