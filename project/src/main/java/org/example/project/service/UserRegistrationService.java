package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.config.KeycloakProperties;
import org.example.project.dto.LoginRequest;
import org.example.project.dto.UserDto;
import org.example.project.entity.enums.Role;
import org.example.project.entity.internal.InboxMessage;
import org.example.project.repository.InboxRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final RestTemplate restTemplate;
    private final InboxRepository inboxRepository;
    private final KeycloakProperties keycloakProperties;

    public AccessTokenResponse getJwt(LoginRequest loginRequest) {
        Keycloak userKeycloak = keycloakProperties.userKeycloak(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        return userKeycloak.tokenManager().getAccessToken();
    }

    @Transactional
    public void registerUser(UserDto newUserDto, Role userRole) {
        InboxMessage inboxMessage = getInboxMessage(newUserDto, userRole);
        inboxRepository.save(inboxMessage);
    }

    public void logout(String refreshToken) {
        String logoutUrl = keycloakProperties.getServerUrl() +
                keycloakProperties.REALMS_PATH + keycloakProperties.getRealm() +
                keycloakProperties.LOGOUT_PATH;

        HttpEntity<MultiValueMap<String, String>> request = createHttpEntity(refreshToken);
        ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to logout: " + response.getBody());
        }
    }

    public AccessTokenResponse updateToken(String refreshToken) {
        String tokenUrl = keycloakProperties.getServerUrl() +
                keycloakProperties.REALMS_PATH + keycloakProperties.getRealm() +
                keycloakProperties.TOKEN_PATH;

        HttpEntity<MultiValueMap<String, String>> request = createHttpEntity(refreshToken);
        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, AccessTokenResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to refresh token: " + response.getBody());
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createHttpEntity(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakProperties.getUserClientId());
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        return new HttpEntity<>(body, headers);
    }

    private InboxMessage getInboxMessage(UserDto newUserDto, Role userRole) {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload;
        ObjectNode node = objectMapper.createObjectNode();
        node.putPOJO("user", newUserDto);
        node.put("role", userRole.name());
        payload = objectMapper.writeValueAsString(node);

        InboxMessage inboxMessage = new InboxMessage();
        inboxMessage.setPayload(payload);

        return inboxMessage;
    }
}