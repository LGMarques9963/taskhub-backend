package com.lgmarques.taskhub.domain.user;

import com.lgmarques.taskhub.domain.user.*;
import com.lgmarques.taskhub.infra.security.TokenData;
import com.lgmarques.taskhub.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void registerNewUserWithCorrectData__shouldRegisterUser() {
        UserData userData = new UserData(
                "John Doe",
                "john@example.com",
                "password123"
        );

        // Mocking user not found
        Mockito.when(userRepository.existsByEmail(eq(userData.email()))).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());

        assertTrue(authenticationService.register(userData));
    }

    @Test
    public void registerNewUserWithExistingEmail__shouldThrowException() {
        UserData userData = new UserData(
                "John Doe",
                "john@example.com",
                "password123"
        );

        // Mocking user already exists
        Mockito.when(userRepository.existsByEmail(eq(userData.email()))).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authenticationService.register(userData));
    }

    @Test
    void registerUserWithInvalidInput__shouldThrowException() {
        UserData userData = new UserData("John Doe", "john@example.com", "'; DROP TABLE users; --");
        assertThrows(RuntimeException.class, () -> authenticationService.register(userData));
    }

//    @Test
//    public void testLoginWithCorrectCredentials() {
//        AuthenticationData authData = new AuthenticationData(
//                "john@example.com",
//                "password123"
//        );
//
//        User mockUser = new User();
//        mockUser.setPassword("hashedPassword123"); // Assume that the password is already hashed
//
//        // Mocking user found
//        Mockito.when(userRepository.findUserByEmail(eq(authData.email()))).thenReturn(mockUser);
//        Mockito.when(passwordEncoder.matches(eq(authData.password()), eq(mockUser.getPassword()))).thenReturn(true);
//
//        TokenData tokenData = authenticationService.login(authData);
//        assertNotNull(tokenData);
//        assertNotNull(tokenData.token());
//    }
//
//    @Test
//    public void testLoginWithIncorrectCredentials() {
//        AuthenticationData authData = new AuthenticationData(
//                "john@example.com",
//                "wrongPassword"
//        );
//
//        User mockUser = new User();
//        mockUser.setPassword("hashedPassword123"); // Assume that the password is already hashed
//
//        // Mocking user found
//        Mockito.when(userRepository.findUserByEmail(eq(authData.email()))).thenReturn(mockUser);
//        Mockito.when(passwordEncoder.matches(eq(authData.password()), eq(mockUser.getPassword()))).thenReturn(false);
//
//        assertThrows(RuntimeException.class, () -> authenticationService.login(authData));
//    }

    @Test
    public void testSecurityAgainstSQLInjection() {
        String userInput = "'; DROP TABLE users; --";
        assertFalse(authenticationService.isSafeInput(userInput));
    }

    @Test
    public void testSecurityAgainstXSSAttack() {
        String userInput = "<script>alert('XSS');</script>";
        assertFalse(authenticationService.isSafeInput(userInput));
   }

    @Test
    public void testLoadUserByUsername() {
        String userEmail = "john@example.com";
        Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(new User(
                1L,
                "John Doe",
                userEmail,
                "hashedPassword123"
        ));
        UserDetails userDetails = authenticationService.loadUserByUsername(userEmail);
        assertNotNull(userDetails);
        assertEquals(userEmail, userDetails.getUsername());
    }
}
