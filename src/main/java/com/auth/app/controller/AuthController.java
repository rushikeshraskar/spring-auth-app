package com.auth.app.controller;

import com.auth.app.entity.User;
import com.auth.app.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username is required");
            return "auth/login";
        }
        if (password == null || password.isEmpty()) {
            model.addAttribute("error", "Password is required");
            return "auth/login";
        }
        
        Optional<User> user = authService.findByUsername(username.trim());

        if (user.isPresent() && authService.validatePassword(password, user.get().getPassword())) {
            session.setAttribute("userId", user.get().getId());
            session.setAttribute("username", user.get().getUsername());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        }
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         Model model) {
        try {
            authService.signUp(username, email, password);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/signup";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
