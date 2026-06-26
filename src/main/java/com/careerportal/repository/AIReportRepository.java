package com.careerportal.repository;
import com.careerportal.entity.AIReport;
import com.careerportal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AIReportRepository extends JpaRepository<AIReport, Long> {
    Optional<AIReport> findByStudent(Student student);
    long count();
}
