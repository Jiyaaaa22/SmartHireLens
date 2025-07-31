<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*, javax.servlet.http.*, javax.servlet.*" %>
<%
    HttpSession sessionObj = request.getSession(false);
    String recruiterEmail = (sessionObj != null) ? (String) sessionObj.getAttribute("recruiterEmail") : null;

    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

    String sql = "SELECT * FROM job_descriptions";
    PreparedStatement pst = conn.prepareStatement(sql);
    ResultSet rs = pst.executeQuery();

    boolean hasJD = rs.isBeforeFirst();  // Check if any JDs exist
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>SmartHireLens - Landing Page</title>
        <link rel="stylesheet" type="text/css" href="css/stylee.css" />
    </head>
    <body>

        <!-- Header -->
        <header class="navbar">
            <div class="logo">SmartHireLens</div>
            <nav>
                <ul>
                    <li><a href="#about">About</a></li>
                    <li><a href="#features">Features</a></li>
                    <!-- <li><a href="#">Contact</a></li> -->
                    <li><a href="login.jsp"><strong>Login</strong></a></li>
                </ul>
            </nav>
        </header>

        <!-- Hero Section -->
        <main class="hero">
            <div class="hero-text">
                <h1>Smart Resume Matching & Hiring Intelligence</h1>
                <p>Upload resumes, match with job descriptions, receive feedback, and hire faster — all in one place.</p>
                <div class="buttons">
                    <a href="recruiter_access.html"  class="btn blue"> Recruiter</a>
                    <a href="./candidate/candidate_access.html" class="btn green">Job Seeker</a>
                </div>
            </div>
           
        </main>
        <!-- Add this section below the hero -->
        <section id="about" class="about-section">
            <h2>About SmartHireLens</h2>
            <p>
                SmartHireLens is an intelligent platform that bridges the gap between job seekers and recruiters.
                It parses resumes, matches them with job descriptions using smart AI techniques, and provides both
                parties with personalized dashboards. Recruiters get the best-matched candidates, and applicants get
                instant feedback and improvement suggestions.
            </p>
        </section>

        <% if (hasJD) { %>
        <section class="jd-section">
            <h2 style="text-align:center;">Available Job Openings</h2>
            <div class="job-cards">
                <% while (rs.next()) {%>
                <div class="job-card">
                    <h3><%= rs.getString("title")%> at <%= rs.getString("company_name")%></h3>
                    <p><strong>Location:</strong> <%= rs.getString("location")%></p>
                    <p><strong>Experience:</strong> <%= rs.getString("experience_required")%></p>
                    <p><strong>Salary:</strong> <%= rs.getString("salary_offered")%></p>
                    <p><strong>Job Type:</strong> <%= rs.getString("job_type")%></p>
                    <p><strong>Skills:</strong> <%= rs.getString("Key_skills")%></p>
                    <p><strong>Description:</strong> <%= rs.getString("description")%></p>
                    <a href="candidate/candidate_login.html?jobId=<%= rs.getInt("id")%>">
    <button>Apply Now</button>
</a>

                </div>
                <% } %>
            </div>


        </section>
        <% } else { %>
        <p style="text-align:center; margin-top:20px;">No job descriptions available yet.</p>
        <% } %>


        <!-- Features Section -->
        <section class="features" id="features">
            <!-- <h2>Key Features</h2> -->
            <div class="feature-grid">


                <div class="feature-box">
                    <h3>📄 Resume Parsing & Scoring</h3>
                    <p>Candidate resumes are analyzed and scored based on job description relevance.</p>
                </div>

                <div class="feature-box">
                    <h3>📊 Recruiter Dashboard</h3>
                    <p>Visual dashboard with filters, resume scores, candidate skills & analytics.</p>
                </div>



                <div class="feature-box">
                    <h3>💡 Single Resume → Multiple JDs</h3>
                    <p>One resume upload can be matched with multiple job openings automatically.</p>
                </div>

                <div class="feature-box">
                    <h3>📥 Auto Shortlisting</h3>
                    <p>Top-matching resumes are auto-sorted and ready to be downloaded by recruiters.</p>
                </div>

            </div>
        </section>
        <%
            rs.close();
            pst.close();
            conn.close();
        %>


    </body>
</html>
