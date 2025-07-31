<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies
    String recruiterEmail = (String) session.getAttribute("recruiterEmail");
    String recruiterName = (String) session.getAttribute("recruiterName");
 if (recruiterEmail == null || recruiterName == null) {
        response.sendRedirect("recruiter_login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Recruiter Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-color: #0f172a;
            --accent-color: #4f46e5;
            --hover-color: #4338ca;
            --bg-color: #f0f4f8;
            --card-color: #ffffff;
            --shadow: rgba(0, 0, 0, 0.1);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: linear-gradient(to right, #e0e7ff, #f0f9ff);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .dashboard {
            background: var(--card-color);
            padding: 50px 40px;
            border-radius: 18px;
            box-shadow: 0 12px 35px var(--shadow);
            max-width: 500px;
            width: 90%;
            text-align: center;
            animation: fadeIn 0.8s ease-in-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h2 {
            font-size: 26px;
            color: var(--primary-color);
            margin-bottom: 15px;
        }

        .welcome-msg {
            font-size: 14px;
            color: #64748b;
            margin-bottom: 30px;
        }

        .btn-group {
            display: flex;
            flex-direction: column;
            gap: 18px;
        }

        .btn-group a {
            display: block;
            padding: 14px;
            background: var(--accent-color);
            color: #fff;
            border: none;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
        }

        .btn-group a:hover {
            background: var(--hover-color);
            transform: translateY(-2px);
            box-shadow: 0 6px 18px rgba(67, 56, 202, 0.4);
        }

        .footer {
            margin-top: 40px;
            font-size: 13px;
            color: #94a3b8;
        }

        /* Responsive */
        @media (max-width: 480px) {
            .dashboard {
                padding: 35px 25px;
            }

            h2 {
                font-size: 22px;
            }

            .btn-group a {
                font-size: 14px;
                padding: 12px;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard">
        <h2>Welcome, Recruiter 👋</h2>
        <div class="welcome-msg">Logged in as <strong><%= recruiterName %></strong></div>

        <div class="btn-group">
            <a href="../recruiter-jd-upload.jsp">📄 Upload Job Description</a>
            <a href="../viewUploadedJDs.jsp">📂 View Uploaded JDs</a>
            <a href="../recruiter_applications.jsp">📁 Uploaded Resumes by candidates</a>
            <a href="../recruiter_jobs.jsp">📊 JD Analysis</a>

            <a href="../LogoutServlet"style="background:#ef4444;">Logout</a>

        </div>

        <div class="footer">
            SmartHireLens © 2025
        </div>
    </div>
        <script>
    window.history.pushState(null, "", window.location.href);
    window.onpopstate = function () {
        window.history.pushState(null, "", window.location.href);
    };
</script>

</body>
</html>
