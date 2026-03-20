package org.example.project.repository;

import org.example.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByConfirmedFalse();

    Optional<User> findByUsername(String username);
}
