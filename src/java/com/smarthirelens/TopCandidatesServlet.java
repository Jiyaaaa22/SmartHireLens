package com.smarthirelens;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;
import org.json.JSONObject;

@WebServlet("/TopCandidatesServlet")
public class TopCandidatesServlet extends HttpServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/smarthirelens";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "BrightFuture1234##";
    private static final String FLASK_API_URL = "http://localhost:5000/analyze_resume";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jobIdParam = request.getParameter("job_id");
        int jobId = Integer.parseInt(jobIdParam);

        // Debug print
        System.out.println("[DEBUG] Received job_id: " + jobId);

        try {
            // ✅ LOAD JDBC DRIVER
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {

                // Step 1: Get recruiter email for this job
                String recruiterEmail = null;
                PreparedStatement recruiterStmt = conn.prepareStatement("SELECT recruiter_email FROM job_descriptions WHERE id = ?");
                recruiterStmt.setInt(1, jobId);
                ResultSet recruiterRs = recruiterStmt.executeQuery();
                if (recruiterRs.next()) {
                    recruiterEmail = recruiterRs.getString("recruiter_email");
                }

                // Step 2: Get all resumes for this job_id
                String fetchResumesQuery = "SELECT * FROM applied_jobs WHERE jd_id = ?";
                PreparedStatement ps = conn.prepareStatement(fetchResumesQuery);
                ps.setInt(1, jobId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int applicationId = rs.getInt("id");
                    String candidateEmail = rs.getString("candidate_email");
                    String resumeFileName = rs.getString("resume_filename");
                    Blob resumeBlob = rs.getBlob("resume_blob");

                    System.out.println("[DEBUG] Parsing resume for: " + candidateEmail);

                    // Convert BLOB to Base64
                    InputStream resumeStream = resumeBlob.getBinaryStream();
                    byte[] resumeBytes = resumeStream.readAllBytes();
                    String base64Resume = Base64.getEncoder().encodeToString(resumeBytes);

                    // Send Base64 resume to Flask API
                    JSONObject flaskResponse = sendToFlaskAPI(base64Resume);

                    if (flaskResponse != null && !flaskResponse.has("error")) {
                        insertIntoResumeAnalysis(conn, candidateEmail, recruiterEmail, jobId, resumeFileName, flaskResponse);
                    } else {
                        System.out.println("[ERROR] Flask returned error or null for: " + candidateEmail);
                    }
                }

                // Step 3: Forward top 10 candidates to JSP
                List<Map<String, Object>> topCandidates = fetchTopCandidates(conn, jobId);
                request.setAttribute("candidates", topCandidates);
                request.getRequestDispatcher("top_candidates_result.jsp").forward(request, response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    private JSONObject sendToFlaskAPI(String base64Resume) {
        try {
            URL url = new URL(FLASK_API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("resume_base64", base64Resume);

            OutputStream os = con.getOutputStream();
            os.write(payload.toString().getBytes());
            os.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            in.close();
            System.out.println("[DEBUG] Flask response received");
            return new JSONObject(result.toString());

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to send to Flask API: " + e.getMessage());
            return null;
        }
    }

    private void insertIntoResumeAnalysis(Connection conn, String email, String recruiterEmail, int jobId, String filename, JSONObject res) {
        try {
            String insertSQL = "INSERT INTO resume_analysis (candidate_email, recruiter_email, job_id, resume_file, skills_score, college_name, college_tier, internship_type, internship_count, internship_score, certification_score, soft_skills_score, experience_score, resume_structure_score, bonus_keyword_score, final_score) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setString(1, email);
            ps.setString(2, recruiterEmail);
            ps.setInt(3, jobId);
            ps.setString(4, filename);
            ps.setDouble(5, res.getDouble("skills_score"));
            ps.setString(6, res.getString("college_name"));
            ps.setInt(7, res.getInt("college_tier"));
            ps.setString(8, res.getString("internship_type"));
            ps.setInt(9, res.getInt("internship_count"));
            ps.setDouble(10, res.getDouble("internship_score"));
            ps.setDouble(11, res.getDouble("certification_score"));
            ps.setDouble(12, res.getDouble("soft_skills_score"));
            ps.setDouble(13, res.getDouble("experience_score"));
            ps.setDouble(14, res.getDouble("resume_structure_score"));
            ps.setDouble(15, res.getDouble("bonus_keyword_score"));
            ps.setDouble(16, res.getDouble("final_score"));

            ps.executeUpdate();
            System.out.println("[DEBUG] Inserted resume_analysis for: " + email);

        } catch (Exception e) {
            System.out.println("[ERROR] Insert failed: " + e.getMessage());
        }
    }

   private List<Map<String, Object>> fetchTopCandidates(Connection conn, int jobId) {
    List<Map<String, Object>> result = new ArrayList<>();
    try {
        // ✅ Join candidates table to get name + final_score from resume_analysis
        String query = "SELECT c.name, ra.candidate_email, ra.resume_file, ra.final_score " +
                       "FROM resume_analysis ra " +
                       "JOIN candidates c ON ra.candidate_email = c.email " +
                       "WHERE ra.job_id = ? " +
                       "ORDER BY ra.final_score DESC " +  // Sorting by score
                       "LIMIT 10";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, jobId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("name", rs.getString("name"));
            candidate.put("candidate_email", rs.getString("candidate_email"));
            candidate.put("resume_file", rs.getString("resume_file"));
            candidate.put("final_score", rs.getFloat("final_score"));
            result.add(candidate);
        }

        System.out.println("[DEBUG] Fetched top candidates with names and scores");

    } catch (Exception e) {
        System.out.println("[ERROR] Fetch top candidates failed: " + e.getMessage());
    }
    return result;
}

}
