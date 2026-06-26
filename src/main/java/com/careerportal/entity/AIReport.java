package com.careerportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AIReport entity - stores AI career analysis results
 */
@Entity
@Table(name = "ai_reports")
public class AIReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String personality;

    @Column(length = 1000)
    private String strengths;

    @Column(length = 1000)
    private String weaknesses;

    @Column(length = 2000)
    private String bestCareers;

    private Integer careerMatchPercent;

    @Column(length = 2000)
    private String skillsToLearn;

    @Column(length = 2000)
    private String certifications;

    @Column(length = 2000)
    private String recommendedCourses;

    @Column(length = 2000)
    private String recommendedJobs;

    @Column(length = 2000)
    private String internshipSuggestions;

    @Column(length = 5000)
    private String careerRoadmap;

    @Column(length = 10000)
    private String fullResponse; // Raw AI response

    private LocalDateTime generatedAt = LocalDateTime.now();

    public AIReport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getPersonality() { return personality; }
    public void setPersonality(String personality) { this.personality = personality; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getBestCareers() { return bestCareers; }
    public void setBestCareers(String bestCareers) { this.bestCareers = bestCareers; }

    public Integer getCareerMatchPercent() { return careerMatchPercent; }
    public void setCareerMatchPercent(Integer careerMatchPercent) { this.careerMatchPercent = careerMatchPercent; }

    public String getSkillsToLearn() { return skillsToLearn; }
    public void setSkillsToLearn(String skillsToLearn) { this.skillsToLearn = skillsToLearn; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getRecommendedCourses() { return recommendedCourses; }
    public void setRecommendedCourses(String recommendedCourses) { this.recommendedCourses = recommendedCourses; }

    public String getRecommendedJobs() { return recommendedJobs; }
    public void setRecommendedJobs(String recommendedJobs) { this.recommendedJobs = recommendedJobs; }

    public String getInternshipSuggestions() { return internshipSuggestions; }
    public void setInternshipSuggestions(String internshipSuggestions) { this.internshipSuggestions = internshipSuggestions; }

    public String getCareerRoadmap() { return careerRoadmap; }
    public void setCareerRoadmap(String careerRoadmap) { this.careerRoadmap = careerRoadmap; }

    public String getFullResponse() { return fullResponse; }
    public void setFullResponse(String fullResponse) { this.fullResponse = fullResponse; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
