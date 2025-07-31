<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Upload Job Description</title>
<!--    <link rel="stylesheet" type="text/css" href="css/upload_jd.css">-->
<style>body {
    margin: 0;
    padding: 0;
    font-family: 'Segoe UI', sans-serif;
    background: linear-gradient(135deg, #dbeafe, #f0f9ff);
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: flex-start;
    padding-top: 50px;
}

.container {
    background: #ffffff;
    width: 90%;
    max-width: 700px;
    padding: 30px 35px;
    border-radius: 15px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

h2 {
    text-align: center;
    color: #1e3a8a;
    margin-bottom: 25px;
    font-weight: 600;
    font-size: 26px;
}

label {
    display: block;
    margin-top: 20px;
    font-weight: 500;
    color: #374151;
    font-size: 15px;
}

input[type="text"],
textarea {
    width: 100%;
    padding: 12px 15px;
    margin-top: 8px;
    border-radius: 10px;
    border: 1px solid #ccc;
    background: #f9fafb;
    font-size: 15px;
    box-sizing: border-box;
}

textarea {
    resize: vertical;
    min-height: 100px;
}

.note {
    font-size: 13px;
    color: #6b7280;
    margin-top: 5px;
}

button {
    width: 100%;
    padding: 14px;
    background-color: #2563eb;
    color: white;
    font-size: 16px;
    font-weight: bold;
    border: none;
    border-radius: 10px;
    margin-top: 30px;
    cursor: pointer;
    transition: background 0.3s ease;
}

button:hover {
    background-color: #1d4ed8;
}

.section-heading {
    margin-top: 35px;
    font-size: 18px;
    color: #111827;
    border-bottom: 1px solid #ddd;
    padding-bottom: 5px;
    font-weight: 600;
}
</style>
</head>
<body>
    <div class="container">
        <h2>Post a Job</h2>
        <form action="uploadJD" method="post">
            <input type="text" name="title" placeholder="Job Title" required><br>
            <input type="text" name="company" placeholder="Company Name" required><br>
            <input type="text" name="location" placeholder="Location" required><br>
            <input type="text" name="experience" placeholder="Experience Required (e.g. 2-4 years)" required><br>
            <input type="text" name="salary" placeholder="Salary Offered (e.g. ₹5-8 LPA)" required><br>
            <select name="jobType" required>
                <option value="">Select Job Type</option>
                <option value="Full-Time">Full-Time</option>
                <option value="Part-Time">Part-Time</option>
                <option value="Internship">Internship</option>
                <option value="Contract">Contract</option>
            </select><br>
            <textarea name="skills" placeholder="Key Skills (comma separated)" required></textarea><br>
            <textarea name="description" placeholder="Job Description" required></textarea><br>
            <input type="submit" value="Upload Job">
        </form>
    </div>
</body>
</html>
