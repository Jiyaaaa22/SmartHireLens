//package com.smarthirelens;
//
//import java.io.*;
//import java.sql.*;
//import javax.servlet.*;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.*;
//
//@WebServlet("/candidate-signup")
//public class CandidateSignupServlet extends HttpServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String name = request.getParameter("name");
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//
//        response.setContentType("text/html");
//        PrintWriter out = response.getWriter();
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection conn = DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##"
//            );
//
//            // Check if email already exists
//            PreparedStatement checkStmt = conn.prepareStatement(
//                "SELECT * FROM candidates WHERE email = ?"
//            );
//            checkStmt.setString(1, email);
//            ResultSet rs = checkStmt.executeQuery();
//
//            if (rs.next()) {
//                // Email already exists
//                out.println("<script>");
//                out.println("alert('Email already exists! Please login.');");
//                out.println("window.location='candidate/candidate_login.html';");
//                out.println("</script>");
//            } else {
//                // Insert new candidate
//                PreparedStatement insertStmt = conn.prepareStatement(
//                    "INSERT INTO candidates (name, email, password) VALUES (?, ?, ?)"
//                );
//                insertStmt.setString(1, name);
//                insertStmt.setString(2, email);
//                insertStmt.setString(3, password);
//
//                int row = insertStmt.executeUpdate();
//
//                if (row > 0) {
//                    out.println("<script>");
//                    out.println("alert('Signup Successful!');");
//                    out.println("window.location='candidate/candidate_dashboard.html';");
//                    out.println("</script>");
//                } else {
//                    out.println("<script>");
//                    out.println("alert('Signup Failed. Try again.');");
//                    out.println("window.location='candidate/candidate_signup.html';");
//                    out.println("</script>");
//                }
//
//                insertStmt.close();
//            }
//
//            rs.close();
//            checkStmt.close();
//            conn.close();
//
//        } catch (Exception e) {
//            out.println("<script>");
//            out.println("alert('Server error: " + e.getMessage() + "');");
//            out.println("window.location='candidate/candidate_signup.html';");
//            out.println("</script>");
//        }
//    }
//}
package com.smarthirelens;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/candidate-signup")
public class CandidateSignupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String jobId = request.getParameter("jobId");  // 👈 Fetch jobId (if user came via "Apply Now")

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##"
            );

            // Check if email already exists
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT * FROM candidates WHERE email = ?"
            );
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Email already exists
                out.println("<script>");
                out.println("alert('Email already exists! Please login.');");
                if (jobId != null && !jobId.trim().isEmpty()) {
                    out.println("window.location='candidate/candidate_login.html?jobId=" + jobId + "';");
                } else {
                    out.println("window.location='candidate/candidate_login.html';");
                }
                out.println("</script>");
            } else {
                // Insert new candidate
                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO candidates (name, email, password) VALUES (?, ?, ?)"
                );
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);

                int row = insertStmt.executeUpdate();

                if (row > 0) {
                    // Store session
                    HttpSession session = request.getSession();
                    session.setAttribute("candidateEmail", email);
                    session.setAttribute("candidateName", name); 

                    if (jobId != null && !jobId.trim().isEmpty()) {
                        response.sendRedirect("resume_apply.jsp?jobId=" + jobId);
                    } else {
                        out.println("<script>");
                        out.println("alert('Signup Successful!');");
                        out.println("window.location='candidate/candidate_dashboard.jsp';");
                        out.println("</script>");
                    }
                } else {
                    out.println("<script>");
                    out.println("alert('Signup Failed. Try again.');");
                    out.println("window.location='candidate/candidate_signup.html';");
                    out.println("</script>");
                }

                insertStmt.close();
            }

            rs.close();
            checkStmt.close();
            conn.close();

        } catch (Exception e) {
            out.println("<script>");
            out.println("alert('Server error: " + e.getMessage() + "');");
            out.println("window.location='candidate/candidate_signup.html';");
            out.println("</script>");
        }
    }
}
