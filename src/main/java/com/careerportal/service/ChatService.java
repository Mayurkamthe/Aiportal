package com.careerportal.service;

import com.careerportal.entity.*;
import com.careerportal.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * ChatService - manages AI career chat
 */
@Service
public class ChatService {

    @Autowired private ChatHistoryRepository chatRepo;
    @Autowired private OpenRouterService openRouterService;

    public List<ChatHistory> getChatHistory(Student student) {
        return chatRepo.findByStudentOrderByChatTimeAsc(student);
    }

    /** Send message to AI and save to history */
    public ChatHistory sendMessage(Student student, String message) {
        // Build context from student profile
        String context = "Student: " + student.getUser().getFullName()
                + ", Class: " + student.getCurrentClass()
                + ", Interests: " + student.getInterests()
                + ", Career Goal: " + student.getCareerGoal();

        // Get AI response
        String aiResponse = openRouterService.careerChat(context, message);

        // Save to history
        ChatHistory chat = new ChatHistory();
        chat.setStudent(student);
        chat.setUserMessage(message);
        chat.setAiResponse(aiResponse);
        return chatRepo.save(chat);
    }
}
