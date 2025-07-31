package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/DownloadResumeServlet")
public class DownloadResumeServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthirelens";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "BrightFuture1234##";

    // Change this to the actual path where resumes are stored
    private static final String RESUME_FOLDER = "C:\\Users\\shivanshu joshi\\Documents\\NetBeansProjects\\smartHireLens\\web\\resumes";  // or "D:/uploads/" etc.

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      String resumeIdStr = request.getParameter("resume_id");


        if (resumeIdStr == null) {
            response.getWriter().println("Resume ID is missing.");
            return;
        }

        int resumeId;
        try {
            resumeId = Integer.parseInt(resumeIdStr);
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid Resume ID.");
            return;
        }

        String fileName = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "SELECT file_name FROM applied_resumes WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, resumeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                fileName = rs.getString("file_name");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            response.getWriter().println("Database Error: " + e.getMessage());
            return;
        }

        if (fileName == null) {
            response.getWriter().println("Resume not found.");
            return;
        }

   File file = new File(RESUME_FOLDER + File.separator + fileName);

        if (!file.exists()) {
            response.getWriter().println("Resume file missing on server.");
            return;
        }

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Stream file to browser
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
