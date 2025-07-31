from flask import Flask, request, jsonify
import os
import json
import mysql.connector
from PyPDF2 import PdfReader
import spacy
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)
nlp = spacy.load("en_core_web_sm")

# ----------------- Utility Functions -------------------

def extract_resume_text(resume_path):
    try:
        reader = PdfReader(resume_path)
        text = ""
        for page in reader.pages:
            page_text = page.extract_text()
            if page_text:
                text += page_text
        return text.strip()
    except Exception as e:
        raise Exception("Failed to extract text from resume: " + str(e))


def extract_skills(text):
    doc = nlp(text)
    return [chunk.text.lower() for chunk in doc.noun_chunks]


def fetch_job_descriptions():
    try:
        conn = mysql.connector.connect(
            host="localhost",
            user="root",
            password="BrightFuture1234##",
            database="smarthirelens"
        )
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT id, title, description FROM job_descriptions")
        jds = cursor.fetchall()
        conn.close()
        return jds
    except Exception as e:
        raise Exception("Failed to fetch job descriptions: " + str(e))


def get_top_matching_jds(resume_text, jds):
    resume_skills = extract_skills(resume_text)
    resume_text_clean = resume_text.lower()

    vectorizer = TfidfVectorizer().fit([resume_text_clean] + [jd['description'].lower() for jd in jds])
    resume_vector = vectorizer.transform([resume_text_clean])

    results = []
    for jd in jds:
        jd_text = jd['title'] + " " + jd['description']
        jd_vector = vectorizer.transform([jd_text.lower()])
        similarity = cosine_similarity(resume_vector, jd_vector)[0][0] * 100
        matched = [skill for skill in resume_skills if skill in jd_text.lower()]
        results.append({
            "jd_id": jd["id"],
            "score": round(similarity, 2),
            "matched_skills": ", ".join(set(matched))
        })

    top_results = sorted(results, key=lambda x: x["score"], reverse=True)[:5]
    return top_results


def load_skill_dict():
    with open("skills.json", "r") as file:
        return json.load(file)


def match_skills_from_json(resume_text, skill_dict):
    text = resume_text.lower()
    matched = []
    for main_skill, variants in skill_dict.items():
        if any(variant in text for variant in variants):
            matched.append(main_skill)
    return matched


# ---------------------- Routes ------------------------

@app.route('/')
def home():
    return "✅ SmartHireLens Resume Analyzer API is running."


@app.route('/analyze', methods=['POST'])
def analyze_resume():
    try:
        if 'resume' not in request.files:
            return jsonify({"error": "No resume file uploaded"}), 400

        file = request.files['resume']
        if file.filename == '':
            return jsonify({"error": "No file selected"}), 400

        os.makedirs("uploads", exist_ok=True)
        resume_path = os.path.join("uploads", file.filename)
        file.save(resume_path)

        resume_text = extract_resume_text(resume_path)
        if not resume_text or resume_text.strip() == "":
            return jsonify({"error": "Resume content unreadable or empty"}), 400

        # ============= Balanced Scoring Logic =============
        skill_dict = load_skill_dict()
        matched_skills = match_skills_from_json(resume_text, skill_dict)
        matched_skill_count = len(matched_skills)

        # ✅ Skill Score (Balanced)
        if matched_skill_count >= 7:
            skill_score = 30
        elif matched_skill_count >= 4:
            skill_score = 20
        elif matched_skill_count >= 2:
            skill_score = 10
        else:
            skill_score = 0

        # ✅ Keyword Score
        keyword_bank = [
            "python", "sql", "cloud", "communication", "project", "internship",
            "problem solving", "java", "teamwork"
        ]
        keyword_count = sum(1 for word in keyword_bank if word in resume_text.lower())
        keyword_score = min(keyword_count * 2, 15)

        # ✅ Education/Experience
        edu_score = 15 if any(x in resume_text.lower() for x in ["education", "experience", "internship"]) else 5

        # ✅ Soft Skills
        soft_skills = ["teamwork", "leadership", "communication", "adaptability", "management"]
        soft_count = sum(1 for s in soft_skills if s in resume_text.lower())
        soft_score = 10 if soft_count >= 3 else (5 if soft_count >= 1 else 0)

        # ✅ Formatting
        format_sections = ["skills", "projects", "education", "experience"]
        format_count = sum(1 for s in format_sections if s in resume_text.lower())
        format_score = 10 if format_count >= 2 else 5

        # ✅ Certifications/Projects
        cert_project_score = 20 if any(x in resume_text.lower() for x in ["certified", "training", "course", "project"]) else 10

        # ✅ Final Score
        overall_score = round(skill_score + keyword_score + edu_score + soft_score + format_score + cert_project_score, 2)

        # Optionally return JD matches (not used in score)
        jds = fetch_job_descriptions()
        matches = get_top_matching_jds(resume_text, jds)

        return jsonify({
            "overall_score": overall_score,
            "matched_skills": matched_skills,
            "matches": matches
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# -------------------- Run Server ----------------------

if __name__ == '__main__':
    app.run(port=5005, debug=True)
