package com.careerportal.service;

import com.careerportal.entity.SystemSettings;
import com.careerportal.repository.SystemSettingsRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * OpenRouterService - handles all AI calls via OpenRouter, using Spring AI's
 * OpenAI-compatible ChatClient (OpenRouter exposes an OpenAI-compatible API,
 * so Spring AI's regular OpenAI client works against it with a custom base URL).
 *
 * The API key/model are still pulled dynamically from Admin -> System Settings
 * on every call (not from application.properties), so the admin panel's
 * "change key without restarting" behavior is preserved.
 */
@Service
public class OpenRouterService {

    @Value("${app.openrouter.base-url}")
    private String baseUrl;

    @Value("${app.openrouter.model}")
    private String defaultModel;

    @Autowired
    private SystemSettingsRepository settingsRepo;

    /**
     * Send a message to OpenRouter AI and get response
     */
    public String chat(String systemPrompt, String userMessage) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.equals("YOUR_API_KEY_HERE") || apiKey.isBlank()) {
            return "⚠️ OpenRouter API key not configured. Please go to Admin → System Settings and add your API key from https://openrouter.ai";
        }

        try {
            ChatClient chatClient = buildChatClient(getModel(), apiKey);

            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .call()
                    .content();

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

    /**
     * Build a fresh ChatClient pointed at OpenRouter, using whatever key/model
     * is currently stored in the DB. Built per-call (not cached as a bean) so
     * an admin changing the key in System Settings takes effect immediately.
     */
    private ChatClient buildChatClient(String model, String apiKey) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .maxTokens(1500)
                        .build())
                .build();

        return ChatClient.builder(chatModel).build();
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
