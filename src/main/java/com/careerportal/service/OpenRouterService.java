package com.careerportal.service;

import com.careerportal.entity.SystemSettings;
import com.careerportal.repository.SystemSettingsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * OpenRouterService - handles all AI API calls via OpenRouter
 * Supports Gemini, GPT, Claude, DeepSeek models
 */
@Service
public class OpenRouterService {

    @Value("${app.openrouter.api-url}")
    private String apiUrl;

    @Value("${app.openrouter.model}")
    private String defaultModel;

    @Autowired
    private SystemSettingsRepository settingsRepo;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Send a message to OpenRouter AI and get response
     */
    public String chat(String systemPrompt, String userMessage) {
        try {
            String apiKey = getApiKey();
            if (apiKey == null || apiKey.equals("YOUR_API_KEY_HERE") || apiKey.isBlank()) {
                return "⚠️ OpenRouter API key not configured. Please go to Admin → System Settings and add your API key from https://openrouter.ai";
            }

            String model = getModel();
            String requestBody = buildRequestBody(systemPrompt, userMessage, model);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:8080")
                    .header("X-Title", "AI Career Portal")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractResponse(response.body());
            } else {
                return "❌ AI Error (Status " + response.statusCode() + "): Please check your API key and try again.";
            }

        } catch (Exception e) {
            return "❌ Error calling AI: " + e.getMessage();
        }
    }

    /**
     * Generate career assessment from student data
     */
    public String generateCareerAssessment(String studentData) {
        String systemPrompt = """
            You are an expert career counselor AI. Analyze the student's profile and assessment data.
            Respond ONLY in this exact JSON format (no markdown, no extra text):
            {
                "personality": "Describe personality type in 2-3 sentences",
                "strengths": "List 4-5 key strengths",
                "weaknesses": "List 2-3 areas to improve",
                "bestCareers": "List top 5 career options with brief reasons",
                "careerMatchPercent": 85,
                "skillsToLearn": "List 5 skills to develop",
                "certifications": "List 3-4 relevant certifications",
                "recommendedCourses": "List 3-4 specific online courses",
                "recommendedJobs": "List 3-4 entry-level job titles",
                "internshipSuggestions": "List 3-4 internship types",
                "careerRoadmap": "Describe a 5-year step-by-step career roadmap"
            }
            """;

        return chat(systemPrompt, "Analyze this student: " + studentData);
    }

    /**
     * Generate assessment questions using AI
     * Returns a JSON array of question objects
     */
    public String generateQuestions(String questionType, String topic, int count) {
        String systemPrompt = """
            You are an expert exam question creator for student career assessments in India.
            Generate exactly the requested number of MCQ questions.
            Respond ONLY with a valid JSON array. No markdown, no explanation, no extra text.
            Each object must have these exact keys:
            {
              "questionText": "The full question text",
              "optionA": "First option",
              "optionB": "Second option",
              "optionC": "Third option",
              "optionD": "Fourth option",
              "correctAnswer": "A"
            }
            For PERSONALITY and INTEREST questions, correctAnswer should be null.
            For APTITUDE questions, correctAnswer must be A, B, C, or D.
            Make questions unique, relevant, and appropriate for 11th/12th grade students.
            """;

        String userMessage = String.format(
            "Generate %d %s type questions%s. Return ONLY the JSON array.",
            count,
            questionType,
            topic != null && !topic.isBlank() ? " on the topic: " + topic : ""
        );

        return chat(systemPrompt, userMessage);
    }

    /**
     * AI chat for career questions
     */
    public String careerChat(String studentContext, String question) {
        String systemPrompt = """
            You are a friendly AI career counselor for students in India.
            Help them with career guidance, course selection, and skill development.
            Be concise, practical, and encouraging.
            Student context: """ + studentContext;

        return chat(systemPrompt, question);
    }

    // Build JSON request body
    private String buildRequestBody(String systemPrompt, String userMessage, String model) {
        return String.format("""
            {
                "model": "%s",
                "max_tokens": 1500,
                "messages": [
                    {"role": "system", "content": %s},
                    {"role": "user", "content": %s}
                ]
            }
            """,
                model,
                toJsonString(systemPrompt),
                toJsonString(userMessage));
    }

    // Extract text from API response
    private String extractResponse(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }

    // Safely escape string for JSON
    private String toJsonString(String text) {
        try {
            return mapper.writeValueAsString(text);
        } catch (Exception e) {
            return "\"" + text.replace("\"", "\\\"") + "\"";
        }
    }

    // Get API key from settings
    private String getApiKey() {
        Optional<SystemSettings> setting = settingsRepo.findBySettingKey("openrouter_api_key");
        return setting.map(SystemSettings::getSettingValue).orElse(null);
    }

    // Get model from settings
    private String getModel() {
        Optional<SystemSettings> setting = settingsRepo.findBySettingKey("openrouter_model");
        return setting.map(SystemSettings::getSettingValue).orElse(defaultModel);
    }
}
