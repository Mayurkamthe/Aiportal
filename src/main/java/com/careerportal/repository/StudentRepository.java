package com.careerportal.repository;

import com.careerportal.entity.Student;
import com.careerportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserId(Long userId);
    List<Student> findByCounselorId(Long counselorId);
    List<Student> findByParentId(Long parentId);
}
