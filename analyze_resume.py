from flask import Flask, request, jsonify
import base64
import os
import tempfile
from PyPDF2 import PdfReader
import spacy

# Load SpaCy model for NLP
nlp = spacy.load("en_core_web_sm")

app = Flask(__name__)

# -------------- Helper Function to Extract Text from PDF ---------------- #
def extract_text_from_pdf(base64_pdf):
    try:
        # Decode base64 string to binary PDF content
        pdf_data = base64.b64decode(base64_pdf)

        # Create temporary file
        with tempfile.NamedTemporaryFile(delete=False, suffix=".pdf") as temp_pdf:
            temp_pdf.write(pdf_data)
            temp_pdf_path = temp_pdf.name

        # Read text using PyPDF2
        reader = PdfReader(temp_pdf_path)
        text = ""
        for page in reader.pages:
            text += page.extract_text() or ""

        os.remove(temp_pdf_path)  # Delete temp file
        return text

    except Exception as e:
        print("[ERROR] Failed to extract PDF:", e)
        return None

# ------------------- Flask Route ------------------- #
@app.route('/analyze_resume', methods=['POST'])
def analyze_resume():
    try:
        data = request.get_json()
        base64_resume = data.get("resume_base64")

        if not base64_resume:
            return jsonify({"error": "Missing resume_base64 field"}), 400

        # Extract raw text from PDF
        resume_text = extract_text_from_pdf(base64_resume)

        if resume_text is None:
            return jsonify({"error": "Failed to extract PDF text"}), 500

        print("[DEBUG] Resume Text Snippet:", resume_text[:200])  # Preview first 200 characters

        # -------------- Perform Real Analysis ---------------- #
        doc = nlp(resume_text.lower())

        # Skills Scoring - lenient logic
        skills_keywords = ["java", "python", "sql", "html", "css", "c++", "c", "javascript", "react", "node", "machine learning", "data", "spring", "django", "flask"]
        skills_found = set()
        for token in doc:
            if token.text in skills_keywords:
                skills_found.add(token.text)
        skills_score = min(10, len(skills_found) * 1.5)  # max 10

        # College Detection
        college_name = "Not Detected"
        college_tier = 3
        for sent in doc.sents:
            if "university" in sent.text or "institute" in sent.text or "college" in sent.text:
                college_name = sent.text.strip()[:100]
                if "iit" in college_name.lower() or "nit" in college_name.lower():
                    college_tier = 1
                elif "university" in college_name.lower():
                    college_tier = 2
                break

        # Internship Scoring - lenient
        internship_keywords = ["intern", "internship", "project"]
        internship_count = sum(1 for sent in doc.sents if any(word in sent.text.lower() for word in internship_keywords))
        internship_score = min(10, internship_count * 3)  # max 10
        internship_type = "Not Mentioned"
        for word in ["remote", "onsite", "hybrid"]:
            if word in resume_text.lower():
                internship_type = word.capitalize()
                break

        # Certification Score - look for certificate mentions
        cert_count = sum(1 for sent in doc.sents if "certi" in sent.text.lower())
        certification_score = min(10, cert_count * 2)  # max 10

        # Soft Skills Detection
        soft_keywords = ["leadership", "teamwork", "communication", "time management", "problem solving", "adaptability"]
        soft_found = set()
        for token in doc:
            if token.text in soft_keywords:
                soft_found.add(token.text)
        soft_skills_score = min(10, len(soft_found) * 2)

        # Experience Scoring
        exp_score = 0
        for sent in doc.sents:
            if "experience" in sent.text.lower():
                exp_score += 4
        experience_score = min(10, exp_score)

        # Resume Structure Score (basic check: has sections)
        structure_score = 0
        sections = ["education", "skills", "experience", "projects", "certifications", "contact"]
        for sec in sections:
            if sec in resume_text.lower():
                structure_score += 1
        resume_structure_score = structure_score / len(sections) * 10  # max 10

        # Bonus Keywords - optional score
        bonus_keywords = ["open source", "hackathon", "github", "freelance", "achievement", "publication"]
        bonus_found = sum(1 for word in bonus_keywords if word in resume_text.lower())
        bonus_keyword_score = min(10, bonus_found * 2)

        # Final Score - more lenient weighted average
        final_score = round(
            skills_score * 0.15 +
            internship_score * 0.15 +
            certification_score * 0.10 +
            soft_skills_score * 0.10 +
            experience_score * 0.10 +
            resume_structure_score * 0.10 +
            bonus_keyword_score * 0.10 +
            (10 - (college_tier - 1) * 3) * 0.10 +  # tier 1 = 10, tier 2 = 7, tier 3 = 4
            8  # base score to make it lenient
        , 2)

        # Final Result
        result = {
            "skills_score": round(skills_score, 2),
            "college_name": college_name,
            "college_tier": college_tier,
            "internship_type": internship_type,
            "internship_count": internship_count,
            "internship_score": round(internship_score, 2),
            "certification_score": round(certification_score, 2),
            "soft_skills_score": round(soft_skills_score, 2),
            "experience_score": round(experience_score, 2),
            "resume_structure_score": round(resume_structure_score, 2),
            "bonus_keyword_score": round(bonus_keyword_score, 2),
            "final_score": final_score
        }

        return jsonify(result)

    except Exception as e:
        print("[SERVER ERROR]", str(e))
        return jsonify({"error": "Internal Server Error"}), 500

# ------------------- Run the Flask App ------------------- #
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
