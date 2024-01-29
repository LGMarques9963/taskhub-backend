import com.lgmarques.taskhub.domain.user.*;
import com.lgmarques.taskhub.infra.security.TokenData;
import com.lgmarques.taskhub.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void testRegisterNewUserWithCorrectData() {
        UserData userData = new UserData();
        userData.setName("John Doe");
        userData.setEmail("john@example.com");
        userData.setPassword("password123");

        // Mocking user not found
        Mockito.when(userRepository.existsByEmail(eq(userData.getEmail()))).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());

        assertTrue(authenticationService.register(userData));
    }

    @Test
    public void testRegisterNewUserWithExistingEmail() {
        UserData userData = new UserData();
        userData.setName("John Doe");
        userData.setEmail("john@example.com");
        userData.setPassword("password123");

        // Mocking user already exists
        Mockito.when(userRepository.existsByEmail(eq(userData.getEmail()))).thenReturn(true);

        assertFalse(authenticationService.register(userData));
    }

    @Test
    public void testLoginWithCorrectCredentials() {
        AuthenticationData authData = new AuthenticationData();
        authData.setEmail("john@example.com");
        authData.setPassword("password123");

        User mockUser = new User();
        mockUser.setPassword("hashedPassword123"); // Assume that the password is already hashed

        // Mocking user found
        Mockito.when(userRepository.findUserByEmail(eq(authData.getEmail()))).thenReturn(mockUser);
        Mockito.when(passwordEncoder.matches(eq(authData.getPassword()), eq(mockUser.getPassword()))).thenReturn(true);

        TokenData tokenData = authenticationService.login(authData);
        assertNotNull(tokenData);
        assertNotNull(tokenData.getToken());
    }

    @Test
    public void testLoginWithIncorrectCredentials() {
        AuthenticationData authData = new AuthenticationData();
        authData.setEmail("john@example.com");
        authData.setPassword("wrongPassword");

        User mockUser = new User();
        mockUser.setPassword("hashedPassword123"); // Assume that the password is already hashed

        // Mocking user found
        Mockito.when(userRepository.findUserByEmail(eq(authData.getEmail()))).thenReturn(mockUser);
        Mockito.when(passwordEncoder.matches(eq(authData.getPassword()), eq(mockUser.getPassword()))).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authenticationService.login(authData));
    }

    @Test
    public void testSecurityAgainstSQLInjection() {
        // Mocking a potential SQL injection attempt
        String userInput = "'; DROP TABLE users; --";
        assertFalse(authenticationService.isSafeInput(userInput));
    }

    @Test
    public void testSecurityAgainstXSSAttack() {
        // Mocking a potential XSS attack attempt
        String userInput = "<script>alert('XSS');</script>";
        assertFalse(authenticationService.isSafeInput(userInput));
    }
}
