
package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/ApplyResumeServlet")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024)  // Max 10MB
public class ApplyResumeServlet extends HttpServlet {

    // Local resume storage path
    private static final String SAVE_DIR = "C:\\Users\\shivanshu joshi\\Documents\\NetBeansProjects\\smartHireLens\\web\\resumes";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String candidateEmail = request.getParameter("candidateEmail");
        String jobIdStr = request.getParameter("jobId");
        Part resumePart = request.getPart("resumeFile");

        if (candidateEmail == null || jobIdStr == null || resumePart == null) {
            response.getWriter().println("Missing parameters");
            return;
        }

        int jobId = Integer.parseInt(jobIdStr);
        InputStream resumeInputStream = resumePart.getInputStream();
        String fileName = resumePart.getSubmittedFileName();

        // 📂 Save file to disk
        File resumeFolder = new File(SAVE_DIR);
        if (!resumeFolder.exists()) resumeFolder.mkdirs(); // Create folder if not exists

        File savedFile = new File(SAVE_DIR + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = resumeInputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        // Reopen stream for DB insert (since stream was already read)
        resumeInputStream = new FileInputStream(savedFile);

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthirelens", "root", "BrightFuture1234##");
String checkSql = "SELECT COUNT(*) FROM applied_resumes WHERE candidate_email = ? AND job_id = ?";
PreparedStatement checkStmt = conn.prepareStatement(checkSql);
checkStmt.setString(1, candidateEmail);
checkStmt.setInt(2, jobId);

ResultSet rs = checkStmt.executeQuery();
if (rs.next() && rs.getInt(1) > 0) {
    // Already applied
    response.sendRedirect("candidate/candidate_dashboard.jsp?msg=resume_already_applied");
    return; // Stop further execution
}
rs.close();
checkStmt.close();
            String sql = "INSERT INTO applied_resumes (candidate_email, job_id, resume_file, file_name) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, candidateEmail);
            stmt.setInt(2, jobId);
            stmt.setBlob(3, resumeInputStream);
            stmt.setString(4, fileName);
  PreparedStatement appliedStmt = conn.prepareStatement(
                    "INSERT INTO applied_jobs (candidate_email, jd_id, resume_filename, resume_blob) VALUES (?, ?, ?, ?)"
            );
            InputStream blobStream = new FileInputStream(savedFile); // new stream
            appliedStmt.setString(1, candidateEmail);
            appliedStmt.setInt(2, jobId);
            appliedStmt.setString(3, fileName);
            appliedStmt.setBlob(4, blobStream);
            appliedStmt.executeUpdate();
            appliedStmt.close();
            blobStream.close();
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                response.sendRedirect("candidate/candidate_dashboard.jsp?msg=Resume+uploaded+successfully");
                response.sendRedirect("candidate/candidate_dashboard.jsp?msg=You+already+applied+to+this+job&error=true");


            } else {
                response.getWriter().println("Resume not uploaded.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                if (resumeInputStream != null) resumeInputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
