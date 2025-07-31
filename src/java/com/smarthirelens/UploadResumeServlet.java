package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/UploadResumeServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class UploadResumeServlet extends HttpServlet {

    private static final String API_URL = "http://localhost:5005/analyze";
    private static final String SAVE_DIR = "C:\\Users\\shivanshu joshi\\Documents\\NetBeansProjects\\smartHireLens\\web\\resumes";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthirelens?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "BrightFuture1234##";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        String candidateEmail = (String) session.getAttribute("candidateEmail");
        if (candidateEmail == null) {
            response.sendRedirect("candidate/candidate_login.html");
            return;
        }

        Part filePart = request.getPart("resume");
        String fileName = new File(filePart.getSubmittedFileName()).getName();
        File resumeFolder = new File(SAVE_DIR);
        if (!resumeFolder.exists()) resumeFolder.mkdirs();
        String filePath = SAVE_DIR + File.separator + fileName;
        filePart.write(filePath);

        File savedFile = new File(filePath);
        if (!savedFile.exists() || savedFile.length() == 0) {
            throw new ServletException("Saved file is empty or missing: " + filePath);
        }

        int resumeId = -1;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL JDBC Driver not found", e);
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO resume_uploads (candidate_email, file_name, file_path) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, candidateEmail);
            ps.setString(2, fileName);
            ps.setString(3, filePath);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) resumeId = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ServletException("Failed to insert resume metadata.", e);
        }

        double overallScore = 0.0;
        List<Map<String, Object>> matches = new ArrayList<>();

        try {
            String boundary = "---MyBoundary" + System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

            File resumeFile = new File(filePath);
            String fileNameForFlask = resumeFile.getName();

            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"resume\"; filename=\"" + fileNameForFlask + "\"").append("\r\n");
            writer.append("Content-Type: application/pdf").append("\r\n");
            writer.append("\r\n").flush();

            FileInputStream inputStream = new FileInputStream(resumeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            inputStream.close();

            writer.append("\r\n").flush();
            writer.append("--" + boundary + "--").append("\r\n");
            writer.close();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                throw new ServletException("Python API call failed with status code: " + responseCode +
                        "\nResponse: " + errorResponse.toString());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder apiResponse = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                apiResponse.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(apiResponse.toString());
            overallScore = json.getDouble("overall_score");
            JSONArray matchArray = json.getJSONArray("matches");

            for (int i = 0; i < matchArray.length(); i++) {
                JSONObject obj = matchArray.getJSONObject(i);
                Map<String, Object> match = new HashMap<>();
                match.put("jd_id", obj.getInt("jd_id"));
                match.put("score", obj.getDouble("score"));
                match.put("matched_skills", obj.getString("matched_skills"));
                matches.add(match);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Python API call failed or response was invalid", e);
        }

        List<Map<String, Object>> matchedJDs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO resume_job_matches (resume_id, jd_id, match_score, matched_skills) VALUES (?, ?, ?, ?)")) {

            for (Map<String, Object> match : matches) {
                ps.setInt(1, resumeId);
                ps.setInt(2, (int) match.get("jd_id"));
                ps.setDouble(3, (double) match.get("score"));
                ps.setString(4, (String) match.get("matched_skills"));
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();

            for (Map<String, Object> match : matches) {
                int jdId = (int) match.get("jd_id");

                PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM job_descriptions WHERE id = ?");
                ps2.setInt(1, jdId);
                ResultSet rs = ps2.executeQuery();

                if (rs.next()) {
                    Map<String, Object> jd = new HashMap<>();
                    jd.put("id", rs.getInt("id"));
                    jd.put("title", rs.getString("title"));
                    jd.put("company", rs.getString("company_name"));
                    jd.put("location", rs.getString("location"));
                    jd.put("experience", rs.getString("experience_required"));
                    jd.put("salary", rs.getString("salary_offered"));
                    jd.put("job_type", rs.getString("job_type"));
                    jd.put("skills", rs.getString("key_skills"));
                    jd.put("description", rs.getString("description"));
                    matchedJDs.add(jd);
                }
                rs.close();
                ps2.close();
            }
        } catch (SQLException e) {
            throw new ServletException("Failed to insert match results or fetch JDs", e);
        }

        request.setAttribute("overallScore", overallScore);
        request.setAttribute("matches", matches);
        request.setAttribute("matchedJDs", matchedJDs);
        request.setAttribute("resumeId", resumeId);
        request.getRequestDispatcher("resume_analysis_result.jsp").forward(request, response);
    }
}
