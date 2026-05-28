package com.neerjaphysio.dto;

import com.neerjaphysio.model.Role;
import com.neerjaphysio.model.User;

public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private Boolean active;

    public UserDTO() {}

    public UserDTO(Long id, String username, String fullName, String role, Boolean active) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getRole().name(),
            user.getActive()
        );
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Boolean getActive() { return active; }
}
