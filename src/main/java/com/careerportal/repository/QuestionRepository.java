package com.careerportal.repository;
import com.careerportal.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionTypeAndActiveOrderByOrderNum(String type, boolean active);
    List<Question> findByQuestionType(String type);
}
