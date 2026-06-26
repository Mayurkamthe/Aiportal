package com.careerportal.repository;
import com.careerportal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByActive(boolean active);
    List<Course> findByCategory(String category);
}
