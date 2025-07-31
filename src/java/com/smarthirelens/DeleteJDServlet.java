package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DeleteJDServlet")
public class DeleteJDServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String jobIdParam = request.getParameter("jobId");

        if (jobIdParam == null || jobIdParam.trim().isEmpty()) {
            response.getWriter().println("Error: Job ID is missing.");
            return;
        }

        try {
            int jobId = Integer.parseInt(jobIdParam);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smarthirelens",
                    "root", "BrightFuture1234##"
            );

            PreparedStatement ps = con.prepareStatement("DELETE FROM job_descriptions WHERE id = ?");
            ps.setInt(1, jobId);
            int rowsAffected = ps.executeUpdate();

            ps.close();
            con.close();

            if (rowsAffected > 0) {
                // Successfully deleted
                response.sendRedirect("viewUploadedJDs.jsp");
            } else {
                response.getWriter().println("Error: JD not found or already deleted.");
            }

        } catch (NumberFormatException e) {
            response.getWriter().println("Error: Invalid job ID.");
        } catch (Exception e) {
            response.getWriter().println("Error deleting JD: " + e.getMessage());
        }
    }

    // Optional: redirect GET requests to JSP or deny
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("viewUploadedJDs.jsp");
    }
}
