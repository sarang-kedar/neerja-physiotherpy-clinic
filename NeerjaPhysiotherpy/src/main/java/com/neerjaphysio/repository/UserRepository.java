package com.neerjaphysio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.Role;
import com.neerjaphysio.model.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByActive(Boolean active);

    boolean existsByUsername(String username);
}
