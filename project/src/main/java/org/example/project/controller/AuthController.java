package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.LoginRequest;
import org.example.project.dto.UserDto;
import org.example.project.entity.enums.Role;
import org.example.project.service.UserRegistrationService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @RequestBody
            UserDto newUserDto,
            @RequestParam(name = "role")
            Role userRole
    ) {
        userRegistrationService.registerUser(newUserDto, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userRegistrationService.getJwt(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        userRegistrationService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update-token")
    public ResponseEntity<AccessTokenResponse> updateToken(@RequestParam String refreshToken) {
        AccessTokenResponse newAccessToken = userRegistrationService.updateToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }
}
