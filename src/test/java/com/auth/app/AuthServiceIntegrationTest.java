package com.auth.app;

import com.auth.app.entity.User;
import com.auth.app.repository.UserRepository;
import com.auth.app.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void tearDown() {
        // Clear all users after each test
        userRepository.deleteAll();
    }

    @Test
    public void testSignUpSuccess() {
        User user = authService.signUp("servicetest", "servicetest@example.com", "password123");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("servicetest");
        assertThat(user.getEmail()).isEqualTo("servicetest@example.com");
        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void testSignUpWithDuplicateUsername() {
        authService.signUp("duplicateuser", "first@example.com", "password123");

        assertThatThrownBy(() -> authService.signUp("duplicateuser", "second@example.com", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");
    }

    @Test
    public void testSignUpWithDuplicateEmail() {
        authService.signUp("user1", "duplicate@example.com", "password123");

        assertThatThrownBy(() -> authService.signUp("user2", "duplicate@example.com", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");
    }

    @Test
    public void testFindByUsername() {
        authService.signUp("finduser", "finduser@example.com", "password123");

        var user = authService.findByUsername("finduser");

        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("finduser");
    }

    @Test
    public void testFindByUsernameNotFound() {
        var user = authService.findByUsername("nonexistentuser123");

        assertThat(user).isEmpty();
    }

    @Test
    public void testValidatePassword() {
        User user = authService.signUp("pwdtest", "pwdtest@example.com", "mypassword");

        boolean isValid = authService.validatePassword("mypassword", user.getPassword());
        boolean isInvalid = authService.validatePassword("wrongpassword", user.getPassword());

        assertThat(isValid).isTrue();
        assertThat(isInvalid).isFalse();
    }

    @Test
    public void testSignUpWithInvalidUsername() {
        assertThatThrownBy(() -> authService.signUp("ab", "test@example.com", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least");
    }

    @Test
    public void testSignUpWithInvalidEmail() {
        assertThatThrownBy(() -> authService.signUp("validuser", "notanemail", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email");
    }

    @Test
    public void testSignUpWithShortPassword() {
        assertThatThrownBy(() -> authService.signUp("validuser", "test@example.com", "short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least");
    }

    @Test
    public void testSignUpWithEmptyUsername() {
        assertThatThrownBy(() -> authService.signUp("", "test@example.com", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    public void testSignUpWithEmptyEmail() {
        assertThatThrownBy(() -> authService.signUp("validuser", "", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    public void testSignUpWithEmptyPassword() {
        assertThatThrownBy(() -> authService.signUp("validuser", "test@example.com", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    public void testSignUpWithDescription() {
        User user = authService.signUp("desctest", "desctest@example.com", "password123", "Test description");

        assertThat(user).isNotNull();
        assertThat(user.getDescription()).isEqualTo("Test description");
    }

    @Test
    public void testSignUpWithNullDescription() {
        User user = authService.signUp("nulltest", "nulltest@example.com", "password123", null);

        assertThat(user).isNotNull();
        assertThat(user.getDescription()).isNull();
    }

    @Test
    public void testSignUpWithEmptyDescription() {
        User user = authService.signUp("emptytest", "emptytest@example.com", "password123", "");

        assertThat(user).isNotNull();
        assertThat(user.getDescription()).isEqualTo("");
    }
}
