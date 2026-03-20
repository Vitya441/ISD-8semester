package org.example.project.config;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.User;
import org.example.project.entity.enums.Role;
import org.example.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("manager").isEmpty()) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(encoder.encode("12345"));
            manager.setRole(Role.MANAGER);
            manager.setConfirmed(true);
            userRepository.save(manager);
        }
    }
}
