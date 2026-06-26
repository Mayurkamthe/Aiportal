package com.careerportal.repository;

import com.careerportal.entity.Assessment;
import com.careerportal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByStudent(Student student);
    Optional<Assessment> findByStudentAndAssessmentType(Student student, String type);
    long countByCompleted(boolean completed);
}
