
package com.smarthirelens;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/uploadJD")
public class UploadJDServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // Set encoding for POST data
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get recruiter email from session
        HttpSession session = request.getSession(false); // false: don't create new if not existing
        String recruiterEmail = (String) session.getAttribute("recruiterEmail");

        if (recruiterEmail == null) {
            out.println("<script>alert('Session expired. Please log in again.'); window.location='recruiter/recruiter_login.html';</script>");
            return;
        }

        // Get form data
        String title = request.getParameter("title");
        String company = request.getParameter("company");
        String location = request.getParameter("location");
        String experience = request.getParameter("experience");
        String salary = request.getParameter("salary");
        String jobType = request.getParameter("jobType");
        String skills = request.getParameter("skills");
        String description = request.getParameter("description");

        try {
            // Connect to DB
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##"
            );

            // Prepare SQL insert
            String query = "INSERT INTO job_descriptions (title, company_name, location,  experience_required, salary_offered, job_type, Key_skills, description, recruiter_email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, title);
            pst.setString(2, company);
            pst.setString(3, location);
            pst.setString(4, experience);
            pst.setString(5, salary);
            pst.setString(6, jobType);
            pst.setString(7, skills);
            pst.setString(8, description);
            pst.setString(9, recruiterEmail);

            // Execute update
            int rowInserted = pst.executeUpdate();

            if (rowInserted > 0) {
                out.println("<script>alert('Job uploaded successfully!'); window.location='index.jsp';</script>");
            } else {
                out.println("<script>alert('Something went wrong while uploading.'); window.location='recruiter-jd-upload.jsp';</script>");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
