package com.neerjaphysio.service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Role;
import com.neerjaphysio.model.User;
import com.neerjaphysio.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Encrypt password using SHA-256
    public String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    // Verify password
    public boolean verifyPassword(String rawPassword, String encryptedPassword) {
        return encryptPassword(rawPassword).equals(encryptedPassword);
    }

    // Create user (SUPER_ADMIN only)
    public User createUser(String username, String fullName, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPassword(encryptPassword(password));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    // Login
    @Transactional(readOnly = true)
    public Optional<User> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getActive() && verifyPassword(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    // Get user by ID
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    // Get user by username
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Get all users by role
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // List all active users
    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        return userRepository.findByActive(true);
    }

    // Update user
    public User updateUser(Long id, String fullName, Role role) {
        User user = getUserById(id);
        user.setFullName(fullName);
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Deactivate user
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Change password
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = getUserById(id);
        if (!verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(encryptPassword(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Initialize super admin if not exists
    public void initializeSuperAdmin() {
        if (userRepository.findByUsername("superadmin").isEmpty()) {
            createUser("superadmin", "Super Administrator", "SuperAdmin@123", Role.SUPER_ADMIN);
        }
    }
}
