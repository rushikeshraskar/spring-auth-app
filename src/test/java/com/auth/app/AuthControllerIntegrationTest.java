package com.auth.app;

import com.auth.app.entity.User;
import com.auth.app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        // Create a test user for login tests
        try {
            authService.signUp("testuser", "test@example.com", "password123");
        } catch (IllegalArgumentException e) {
            // User already exists, ignore
        }
    }

    @Test
    public void testLoginPageLoad() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign In")));
    }

    @Test
    public void testSignupPageLoad() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign Up")));
    }

    @Test
    public void testSuccessfulSignup() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign In")))
                .andExpect(content().string(containsString("Account created successfully")));
    }

    @Test
    public void testSignupWithExistingUsername() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "testuser")
                .param("email", "another@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Username already exists")));
    }

    @Test
    public void testSignupWithExistingEmail() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "anotheruser")
                .param("email", "test@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email already exists")));
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
                .andExpect(content().string(containsString("Invalid username or password")));
    }

    @Test
    public void testFailedLoginInvalidUsername() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "nonexistentuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid username or password")));
    }

    @Test
    public void testDashboardWithoutLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testLogout() throws Exception {
        // First login
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "password123"));

        // Then verify session is invalidated after logout
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
