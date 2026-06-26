package com.careerportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** CounselorNote entity - notes added by counselors for students */
@Entity
@Table(name = "counselor_notes")
public class CounselorNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long counselorId;
    private Long studentId;

    @Column(length = 2000)
    private String noteText;

    private LocalDateTime createdAt = LocalDateTime.now();

    public CounselorNote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
