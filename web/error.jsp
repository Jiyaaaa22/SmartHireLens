<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #fff0f0;
            color: #b30000;
            padding: 40px;
        }
        .container {
            background: #ffe6e6;
            padding: 25px 35px;
            border-radius: 10px;
            border: 1px solid #ffcccc;
            max-width: 600px;
            margin: auto;
        }
        a {
            display: inline-block;
            margin-top: 20px;
            text-decoration: none;
            color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Oops! Something went wrong 😢</h2>
        <p>${error}</p>
        <a href="index.jsp">← Back to Home</a>
    </div>
</body>
</html>
