package com.careerportal.service;

import com.careerportal.entity.*;
import com.careerportal.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AssessmentService - manages assessment submission and AI report generation
 */
@Service
public class AssessmentService {

    @Autowired private AssessmentRepository assessmentRepo;
    @Autowired private QuestionRepository questionRepo;
    @Autowired private AIReportRepository aiReportRepo;
    @Autowired private OpenRouterService openRouterService;

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Question> getQuestions(String type) {
        return questionRepo.findByQuestionTypeAndActiveOrderByOrderNum(type, true);
    }

    public List<Assessment> getStudentAssessments(Student student) {
        return assessmentRepo.findByStudent(student);
    }

    public Optional<Assessment> findByStudentAndType(Student student, String type) {
        return assessmentRepo.findByStudentAndAssessmentType(student, type);
    }

    /** Save assessment answers and calculate score */
    public Assessment saveAssessment(Student student, String type, Map<String, String> answers) {
        Assessment assessment = assessmentRepo.findByStudentAndAssessmentType(student, type)
                .orElse(new Assessment());

        assessment.setStudent(student);
        assessment.setAssessmentType(type);
        assessment.setCompleted(true);
        assessment.setCompletedAt(LocalDateTime.now());

        // Calculate score for aptitude
        int score = 0;
        if ("APTITUDE".equals(type)) {
            List<Question> questions = getQuestions(type);
            for (Question q : questions) {
                String answer = answers.get("q_" + q.getId());
                if (q.getCorrectAnswer() != null && q.getCorrectAnswer().equals(answer)) {
                    score++;
                }
            }
        } else {
            score = answers.size(); // Completed count for personality/interest
        }

        assessment.setTotalScore(score);

        // Save answers as JSON
        try {
            assessment.setAnswersJson(mapper.writeValueAsString(answers));
        } catch (Exception e) {
            assessment.setAnswersJson("{}");
        }

        return assessmentRepo.save(assessment);
    }

    /** Check if all 3 assessments are complete */
    public boolean allAssessmentsComplete(Student student) {
        List<Assessment> list = assessmentRepo.findByStudent(student);
        List<String> completed = list.stream()
                .filter(Assessment::isCompleted)
                .map(Assessment::getAssessmentType)
                .toList();
        return completed.contains("PERSONALITY") && completed.contains("INTEREST") && completed.contains("APTITUDE");
    }

    /** Generate AI report after all assessments done */
    public AIReport generateAIReport(Student student) {
        // Build student data summary for AI
        String studentData = buildStudentDataSummary(student);

        // Call AI
        String aiResponse = openRouterService.generateCareerAssessment(studentData);

        // Parse AI response and save
        AIReport report = aiReportRepo.findByStudent(student).orElse(new AIReport());
        report.setStudent(student);
        report.setFullResponse(aiResponse);
        report.setGeneratedAt(LocalDateTime.now());

        // Try to parse JSON response
        try {
            var json = mapper.readTree(aiResponse);
            report.setPersonality(getJsonField(json, "personality"));
            report.setStrengths(getJsonField(json, "strengths"));
            report.setWeaknesses(getJsonField(json, "weaknesses"));
            report.setBestCareers(getJsonField(json, "bestCareers"));
            report.setSkillsToLearn(getJsonField(json, "skillsToLearn"));
            report.setCertifications(getJsonField(json, "certifications"));
            report.setRecommendedCourses(getJsonField(json, "recommendedCourses"));
            report.setRecommendedJobs(getJsonField(json, "recommendedJobs"));
            report.setInternshipSuggestions(getJsonField(json, "internshipSuggestions"));
            report.setCareerRoadmap(getJsonField(json, "careerRoadmap"));
            try {
                report.setCareerMatchPercent(json.path("careerMatchPercent").asInt(75));
            } catch (Exception e) {
                report.setCareerMatchPercent(75);
            }
        } catch (Exception e) {
            // If JSON parsing fails, store raw response
            report.setPersonality("Analysis complete - see full report");
            report.setCareerMatchPercent(75);
        }

        return aiReportRepo.save(report);
    }

    public Optional<AIReport> getAIReport(Student student) {
        return aiReportRepo.findByStudent(student);
    }

    public long countCompletedAssessments() {
        return assessmentRepo.countByCompleted(true);
    }

    public long countReports() {
        return aiReportRepo.count();
    }

    private String buildStudentDataSummary(Student s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(s.getUser().getFullName()).append("\n");
        sb.append("Class: ").append(s.getCurrentClass()).append("\n");
        sb.append("Board: ").append(s.getBoard()).append("\n");
        sb.append("Overall %: ").append(s.getOverallPercentage()).append("\n");
        sb.append("Math: ").append(s.getMathMarks()).append(", Science: ").append(s.getScienceMarks())
          .append(", English: ").append(s.getEnglishMarks()).append("\n");
        sb.append("Skills: ").append(s.getSkills()).append("\n");
        sb.append("Interests: ").append(s.getInterests()).append("\n");
        sb.append("Hobbies: ").append(s.getHobbies()).append("\n");
        sb.append("Career Goal: ").append(s.getCareerGoal()).append("\n");

        // Add assessment summaries
        List<Assessment> assessments = assessmentRepo.findByStudent(s);
        for (Assessment a : assessments) {
            sb.append(a.getAssessmentType()).append(" Score: ").append(a.getTotalScore()).append("\n");
        }
        return sb.toString();
    }

    private String getJsonField(com.fasterxml.jackson.databind.JsonNode node, String field) {
        return node.path(field).asText("Not available");
    }
}
