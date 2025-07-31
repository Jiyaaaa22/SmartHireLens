<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Select Login Type - SmartHireLens</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f0f4ff;
            margin: 0;
            padding: 0;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #ffffff;
            padding: 50px 40px;
            border-radius: 16px;
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
            text-align: center;
            width: 90%;
            max-width: 400px;
        }

        .container h2 {
            margin-bottom: 30px;
            color: #333;
        }

        .login-option {
            display: block;
            margin: 15px auto;
            padding: 14px 28px;
            width: 80%;
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            color: white;
            border-radius: 10px;
            transition: 0.3s ease;
        }

        .recruiter {
            background-color: #28a745;
        }

        .recruiter:hover {
            background-color: #1e7e34;
        }

        .candidate {
            background-color: #007bff;
        }

        .candidate:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Select Login Type</h2>
    <a href="recruiter/recruiter_login.html" class="login-option recruiter">Login as Recruiter</a>
    <a href="candidate/candidate_login.html" class="login-option candidate">Login as Candidate</a>
</div>

</body>
</html>
