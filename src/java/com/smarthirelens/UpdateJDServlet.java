package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/UpdateJDServlet")
public class UpdateJDServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Read form parameters
        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String company = request.getParameter("company_name");
        String location = request.getParameter("location");
        String experience = request.getParameter("experience_required");
        String salary = request.getParameter("salary_offered");
        String jobType = request.getParameter("job_type");
        String keySkills = request.getParameter("key_skills");
        String description = request.getParameter("description");

        if (idStr == null || idStr.isEmpty()) {
            response.getWriter().println("Error: Job ID missing.");
            return;
        }

        try {
            int jobId = Integer.parseInt(idStr);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");

            String sql = "UPDATE job_descriptions SET title=?, company_name=?, location=?, experience_required=?, salary_offered=?, job_type=?, key_skills=?, description=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, company);
            ps.setString(3, location);
            ps.setString(4, experience);
            ps.setString(5, salary);
            ps.setString(6, jobType);
            ps.setString(7, keySkills);
            ps.setString(8, description);
            ps.setInt(9, jobId);

            int rowsUpdated = ps.executeUpdate();
            ps.close();
            conn.close();

            if (rowsUpdated > 0) {
                response.sendRedirect("recruiter/recruiter-dashboard.jsp");  // redirect back to JD list
            } else {
                response.getWriter().println("Failed to update JD. ID may not exist.");
            }

        } catch (NumberFormatException e) {
            response.getWriter().println("Error: Invalid job ID.");
        } catch (Exception e) {
            response.getWriter().println("Error updating JD: " + e.getMessage());
        }
    }
}
