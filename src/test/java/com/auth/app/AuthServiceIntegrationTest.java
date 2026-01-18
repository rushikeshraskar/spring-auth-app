package com.auth.app;

import com.auth.app.entity.User;
import com.auth.app.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

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
}
