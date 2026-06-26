package com.careerportal.repository;
import com.careerportal.entity.CounselorNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CounselorNoteRepository extends JpaRepository<CounselorNote, Long> {
    List<CounselorNote> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<CounselorNote> findByCounselorId(Long counselorId);
}
