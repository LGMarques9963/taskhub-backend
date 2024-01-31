package com.lgmarques.taskhub.controller;

import com.lgmarques.taskhub.domain.user.AuthenticationData;
import com.lgmarques.taskhub.domain.user.AuthenticationService;
import com.lgmarques.taskhub.domain.user.User;
import com.lgmarques.taskhub.domain.user.UserData;
import com.lgmarques.taskhub.infra.security.TokenData;
import com.lgmarques.taskhub.infra.security.TokenService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void testAuthenticateWithValidCredentials__shouldReturnTokenJWT() {
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(tokenService.generateToken(any())).thenReturn("token123");

        AuthenticationData authenticationData = new AuthenticationData("john@example.com", "password123");
        ResponseEntity<TokenData> response = authenticationController.authenticate(authenticationData);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token123", response.getBody().token());
    }

    @Test
    void testAuthenticateWithInvalidCredentials__shouldThrowException() {
        Mockito.when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Invalid credentials") {
        });

        AuthenticationData authenticationData = new AuthenticationData("john@example.com", "password123");
        assertThrows(AuthenticationException.class, () -> authenticationController.authenticate(authenticationData));
    }

    @Test
    void testRegisterWithValidData__shouldReturnOk() {
        Mockito.when(authenticationService.register(any())).thenReturn(true);

        ResponseEntity<?> response = authenticationController.register(
                new UserData(
                        "John Doe",
                        "john@example.com",
                        "password123"
                )
        );
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void testRegisterWithInvalidData__shouldReturnBadRequest() {
        Mockito.when(authenticationService.register(any())).thenThrow(new RuntimeException("Invalid input"));

        ResponseEntity<?> response = authenticationController.register(
                new UserData(
                        "John Doe",
                        "DROP TABLE users; --",
                        "password123"
                )
        );
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid input", response.getBody());
    }

    @Test
    void testRegisterWithExistingEmail__shouldReturnBadRequest() {
        Mockito.when(authenticationService.register(any())).thenThrow(new RuntimeException("E-mail already registered"));

        ResponseEntity<?> response = authenticationController.register(
                new UserData(
                        "John Doe",
                        "john@example.com",
                        "password123"
                )
        );
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("E-mail already registered", response.getBody());
    }
}