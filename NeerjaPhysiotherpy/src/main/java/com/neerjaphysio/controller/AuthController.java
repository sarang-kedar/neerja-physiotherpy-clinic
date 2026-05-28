package com.neerjaphysio.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neerjaphysio.dto.ApiResponse;
import com.neerjaphysio.dto.UserDTO;
import com.neerjaphysio.model.Role;
import com.neerjaphysio.model.User;
import com.neerjaphysio.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

    @Value("${app.session.idle-timeout-ms:600000}")
    private long idleTimeoutMs;

    @Value("${app.session.warning-before-logout-ms:60000}")
    private long warningBeforeLogoutMs;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestParam String username,
            @RequestParam String password) {

        Optional<User> user = userService.authenticate(username, password);
        if (user.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("user", UserDTO.fromEntity(user.get()));
            response.put("sessionId", "SESSION_" + System.nanoTime());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } else {
            return ResponseEntity.ok(ApiResponse.failure("Invalid username or password"));
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @RequestParam String currentUserRole, // SUPER_ADMIN only
            @RequestParam String username,
            @RequestParam String fullName,
            @RequestParam String password,
            @RequestParam String role) {

        // Only SUPER_ADMIN can create users
        if (!currentUserRole.equals(Role.SUPER_ADMIN.name())) {
            return ResponseEntity.ok(ApiResponse.failure("Only Super Admin can create users"));
        }

        try {
            Role userRole = Role.valueOf(role);
            User newUser = userService.createUser(username, fullName, password, userRole);
            return ResponseEntity.ok(ApiResponse.success("User created successfully", UserDTO.fromEntity(newUser)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.failure("Error creating user: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(
            @RequestParam String userRole) {

        // Only SUPER_ADMIN can view all users
        if (!userRole.equals(Role.SUPER_ADMIN.name())) {
            return ResponseEntity.ok(ApiResponse.failure("Unauthorized"));
        }

        List<UserDTO> users = userService.getAllActiveUsers().stream()
                .map(UserDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @PostMapping("/deactivate-user")
    public ResponseEntity<ApiResponse<?>> deactivateUser(
            @RequestParam String currentUserRole,
            @RequestParam Long userId) {

        // Only SUPER_ADMIN can deactivate users
        if (!currentUserRole.equals(Role.SUPER_ADMIN.name())) {
            return ResponseEntity.ok(ApiResponse.failure("Unauthorized"));
        }

        try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.failure(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.failure(e.getMessage()));
        }
    }

    @GetMapping("/verify-session")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifySession(
            @RequestParam String sessionId,
            @RequestParam String username) {

        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent() && user.get().getActive()) {
            Map<String, Object> response = new HashMap<>();
            response.put("user", UserDTO.fromEntity(user.get()));
            response.put("valid", true);
            return ResponseEntity.ok(ApiResponse.success("Session valid", response));
        } else {
            return ResponseEntity.ok(ApiResponse.failure("Invalid or expired session"));
        }
    }

    @GetMapping("/session-config")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getSessionConfig() {
        Map<String, Long> config = new HashMap<>();
        config.put("idleTimeoutMs", idleTimeoutMs);
        config.put("warningBeforeLogoutMs", warningBeforeLogoutMs);
        return ResponseEntity.ok(ApiResponse.success("Session configuration retrieved", config));
    }
}
