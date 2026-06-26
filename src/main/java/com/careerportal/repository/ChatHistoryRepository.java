package com.careerportal.repository;
import com.careerportal.entity.ChatHistory;
import com.careerportal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByStudentOrderByChatTimeAsc(Student student);
}
