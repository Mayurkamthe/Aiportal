package com.careerportal.service;

import com.careerportal.entity.Student;
import com.careerportal.entity.User;
import com.careerportal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * StudentService - manages student profiles
 */
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    public Optional<Student> findByUser(User user) {
        return studentRepo.findByUser(user);
    }

    public Optional<Student> findByUserId(Long userId) {
        return studentRepo.findByUserId(userId);
    }

    public Optional<Student> findById(Long id) {
        return studentRepo.findById(id);
    }

    public List<Student> findAll() {
        return studentRepo.findAll();
    }

    public List<Student> findByCounselorId(Long counselorId) {
        return studentRepo.findByCounselorId(counselorId);
    }

    public List<Student> findByParentId(Long parentId) {
        return studentRepo.findByParentId(parentId);
    }

    public Student save(Student student) {
        // Recalculate profile completion
        student.setProfileCompletion(calculateCompletion(student));
        return studentRepo.save(student);
    }

    /** Upload profile photo and return filename */
    public String uploadPhoto(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(uploadPath);

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    /** Calculate profile completion percentage */
    private int calculateCompletion(Student s) {
        int score = 0;
        if (s.getDateOfBirth() != null && !s.getDateOfBirth().isBlank()) score += 10;
        if (s.getGender() != null && !s.getGender().isBlank()) score += 10;
        if (s.getAddress() != null && !s.getAddress().isBlank()) score += 10;
        if (s.getProfilePhoto() != null && !s.getProfilePhoto().isBlank()) score += 10;
        if (s.getCurrentClass() != null && !s.getCurrentClass().isBlank()) score += 10;
        if (s.getSchool() != null && !s.getSchool().isBlank()) score += 10;
        if (s.getOverallPercentage() != null) score += 10;
        if (s.getSkills() != null && !s.getSkills().isBlank()) score += 10;
        if (s.getInterests() != null && !s.getInterests().isBlank()) score += 10;
        if (s.getCareerGoal() != null && !s.getCareerGoal().isBlank()) score += 10;
        return score;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
