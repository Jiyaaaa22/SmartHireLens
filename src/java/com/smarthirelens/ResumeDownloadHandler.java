package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/ResumeDownloadHandler")
public class ResumeDownloadHandler extends HttpServlet {

    // Folder path where resumes are stored
    private static final String RESUME_FOLDER = "C:\\Users\\shivanshu joshi\\Documents\\NetBeansProjects\\smartHireLens\\web\\resumes";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getParameter("filename");

        if (fileName == null || fileName.trim().isEmpty()) {
            response.getWriter().println("Missing or invalid filename.");
            return;
        }

        // Prevent path traversal
        fileName = new File(fileName).getName();

        File file = new File(RESUME_FOLDER + File.separator + fileName);

        if (!file.exists() || file.isDirectory()) {
            response.getWriter().println("File not found on server.");
            return;
        }

        // Force file download
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Stream the file
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
