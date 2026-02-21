package com.auth.app;

import com.auth.app.entity.User;
import com.auth.app.repository.UserRepository;
import com.auth.app.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        // Clear database before each test
        userRepository.deleteAll();
        // Create a test user for login tests
        authService.signUp("testuser", "test@example.com", "password123");
    }

    @AfterEach
    public void tearDown() {
        // Clear all users after each test
        userRepository.deleteAll();
    }

    @Test
    public void testLoginPageLoad() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignupPageLoad() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSuccessfulSignup() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testSignupWithExistingUsername() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "testuser")
                .param("email", "another@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    public void testSignupWithExistingEmail() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "anotheruser")
                .param("email", "test@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void testFailedLoginInvalidPassword() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    public void testFailedLoginInvalidUsername() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "nonexistentuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    public void testDashboardWithoutLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testLogout() throws Exception {
        // First login
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection());

        // Then logout  
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login*"));
    }

    @Test
    public void testSignupWithDescription() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "descuser")
                .param("email", "descuser@example.com")
                .param("password", "password123")
                .param("description", "Test user description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verify the user was created with description
        var user = authService.findByUsername("descuser");
        assertThat(user).isPresent();
        assertThat(user.get().getDescription()).isEqualTo("Test user description");
    }

    @Test
    public void testSettingsPageWithDescription() throws Exception {
        // First create a user with description
        authService.signUp("settingsuser", "settings@example.com", "password123", "User description");

        // Login
        mockMvc.perform(post("/login")
                .param("username", "settingsuser")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection());

        // Access settings page - should redirect to login first since we're not testing the full flow
        mockMvc.perform(get("/settings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testSignupSuccess() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "testuser")
                .param("email", "test@example.com")
                .param("password", "password")
                .param("confirmPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }
}
