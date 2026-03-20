package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.UserDto;
import org.example.project.entity.User;
import org.example.project.entity.enums.Role;
import org.example.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConfirmed(false);
        repository.save(user);
    }

    public List<UserDto> getPendingUsers() {
        // Находим всех, у кого роль еще не назначена (или по флагу confirmed)
        return repository.findAllByConfirmedFalse().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.isConfirmed()
                ))
                .collect(Collectors.toList());
    }

    public void confirmUser(Long userId) {
        User user = repository.findById(userId).orElseThrow();
        user.setRole(Role.EMPLOYEE);
        user.setConfirmed(true);
        repository.save(user);
    }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
