//package com.smarthirelens;
//
//import java.io.*;
//import java.sql.*;
//import javax.servlet.*;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.*;
//
//@WebServlet("/CandidateLoginServlet")
//public class CandidateLoginServlet extends HttpServlet {
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//
//        response.setContentType("text/html");
//        PrintWriter out = response.getWriter();
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection conn = DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");
//
//            String query = "SELECT * FROM candidates WHERE email=? AND password=?";
//            PreparedStatement stmt = conn.prepareStatement(query);
//            stmt.setString(1, email);
//            stmt.setString(2, password);
//
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                // ✅ Login Success
//                HttpSession session = request.getSession();
//                session.setAttribute("candidateEmail", email);
//
//                response.sendRedirect("candidate/candidate_dashboard.html");
//            } else {
//                // ❌ Login Failed
//                out.println("<script>alert('Invalid Email or Password'); window.location='candidate/candidate_login.html';</script>");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            out.println("<h3>Error: " + e.getMessage() + "</h3>");
//        }
//    }
//}
package com.smarthirelens;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/CandidateLoginServlet")
public class CandidateLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String jobId = request.getParameter("jobId");  // 👈 Get jobId from query (if any)

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

            String query = "SELECT * FROM candidates WHERE email=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // ✅ Login Success
                HttpSession session = request.getSession();
                session.setAttribute("candidateEmail", email);
                 String name = rs.getString("name");  // fetch name from DB
    session.setAttribute("candidateName", name);  // store in session


                // 🔁 Redirect to resume apply if jobId exists
                if (jobId != null && !jobId.trim().isEmpty()) {
                    response.sendRedirect("resume_apply.jsp?jobId=" + jobId);
                } else {
                    response.sendRedirect("candidate/candidate_dashboard.jsp");
                }
            } else {
                // ❌ Login Failed
                out.println("<script>alert('Invalid Email or Password'); window.location='candidate/candidate_login.html';</script>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
