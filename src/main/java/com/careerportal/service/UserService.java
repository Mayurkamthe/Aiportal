package com.careerportal.service;

import com.careerportal.entity.User;
import com.careerportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * UserService - handles user login and management
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    /** Authenticate user with username and password */
    public Optional<User> login(String username, String password) {
        return userRepo.findByUsernameAndPassword(username, password);
    }

    /** Get user by ID */
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    /** Get all users */
    public List<User> findAll() {
        return userRepo.findAll();
    }

    /** Get users by role */
    public List<User> findByRole(String role) {
        return userRepo.findAll().stream()
                .filter(u -> role.equals(u.getRole()))
                .toList();
    }

    /** Save user */
    public User save(User user) {
        return userRepo.save(user);
    }

    /** Delete user */
    public void delete(Long id) {
        userRepo.deleteById(id);
    }

    /** Count users by role */
    public long countByRole(String role) {
        return userRepo.countByRole(role);
    }
}
