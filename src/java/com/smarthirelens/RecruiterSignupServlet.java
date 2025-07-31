package com.smarthirelens;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RecruiterSignupServlet")
public class RecruiterSignupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String company = request.getParameter("company");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement pstCheck = null;
        PreparedStatement pstInsert = null;

        try {
            // Step 1: Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Connect to DB
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

            // Step 3: Check if email already exists
            String checkQuery = "SELECT * FROM recruiters WHERE email = ?";
            pstCheck = conn.prepareStatement(checkQuery);
            pstCheck.setString(1, email);

            ResultSet rs = pstCheck.executeQuery();

            if (rs.next()) {
                // Email already registered
                out.println("<script>alert('Email already registered! Please login.'); window.location='recruiter/recruiter_login.html';</script>");
            } else {
                // Insert new recruiter
                String insertQuery = "INSERT INTO recruiters (name, email, password, company) VALUES (?, ?, ?, ?)";
                pstInsert = conn.prepareStatement(insertQuery);
                pstInsert.setString(1, name);
                pstInsert.setString(2, email);
                pstInsert.setString(3, password);
                pstInsert.setString(4, company);

                int i = pstInsert.executeUpdate();

                if (i > 0) {
                    out.println("<script>alert('Signup successful! Please login now.'); window.location='recruiter/recruiter_login.html';</script>");
                } else {
                    out.println("<script>alert('Signup failed. Try again!'); window.location='recruiter/recruiter_signup.html';</script>");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            try { if (pstCheck != null) pstCheck.close(); } catch (Exception e) {}
            try { if (pstInsert != null) pstInsert.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
