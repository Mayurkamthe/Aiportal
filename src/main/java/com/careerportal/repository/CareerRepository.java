package com.careerportal.repository;
import com.careerportal.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByActive(boolean active);
    List<Career> findByCategory(String category);
}
