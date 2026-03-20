package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.UserDto;
import org.example.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/pending")
    public ResponseEntity<List<UserDto>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<Void> confirmUser(@PathVariable Long id) {
        userService.confirmUser(id);
        return ResponseEntity.ok().build();
    }
}
