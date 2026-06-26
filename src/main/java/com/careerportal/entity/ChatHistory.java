package com.careerportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ChatHistory entity - stores AI career chat messages
 */
@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(length = 2000)
    private String userMessage;

    @Column(length = 5000)
    private String aiResponse;

    private LocalDateTime chatTime = LocalDateTime.now();

    public ChatHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public LocalDateTime getChatTime() { return chatTime; }
    public void setChatTime(LocalDateTime chatTime) { this.chatTime = chatTime; }
}
