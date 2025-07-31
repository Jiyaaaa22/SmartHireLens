<%@ page import="java.util.*, java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <title>Top Candidates</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #eef1f8;
            padding: 40px;
        }

        h2 {
            text-align: center;
            color: #2c3e50;
        }

        table {
            width: 90%;
            margin: 30px auto;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 0 12px #ccc;
        }

        th, td {
            padding: 12px 18px;
            border: 1px solid #ccc;
            text-align: left;
        }

        th {
            background-color: #4e73df;
            color: white;
        }

        a.download-link {
            text-decoration: none;
            color: #2980b9;
        }
    </style>
</head>
<body>

<h2>Top 10 Candidates for This Job</h2>

<%
    List<Map<String, Object>> candidates = (List<Map<String, Object>>) request.getAttribute("candidates");

    if (candidates == null || candidates.isEmpty()) {
%>
        <p style="text-align: center; color: red;">No analyzed resumes found.</p>
<%
    } else {
%>

    <table>
        <thead>
            <tr>
                <th>Candidate Name</th>
                <th>Candidate Email</th>
                <th>Resume File</th>
<!--                <th>Final Score</th>-->
            </tr>
        </thead>
        <tbody>
        <% for (Map<String, Object> row : candidates) { %>
            <tr>
                <td><%= row.get("name") %></td>
                <td><%= row.get("candidate_email") %></td>
                <td>
                    <% String resumeFile = (String) row.get("resume_file"); %>
                    <a class="download-link" href="ResumeDownloadHandler?filename=<%= resumeFile %>" target="_blank"><%= resumeFile %></a>

                </td>
<!--                <td><%= row.get("final_score") %></td>-->
            </tr>
        <% } %>
        </tbody>
    </table>

<%
    }
%>

</body>
</html>
