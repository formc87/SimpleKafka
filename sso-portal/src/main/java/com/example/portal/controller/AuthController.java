package com.example.portal.controller;

import com.example.portal.model.User;
import com.example.portal.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/login"})
    public String loginForm(Model model, HttpSession session) {
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/portal";
        }
        model.addAttribute("loginRequest", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") User loginRequest,
                        Model model,
                        HttpSession session) {
        return userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword())
                .map(user -> {
                    session.setAttribute("loginUser", user);
                    return "redirect:/portal";
                })
                .orElseGet(() -> {
                    model.addAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다");
                    return "login";
                });
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           Model model) {
        if (userService.emailExists(user.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일입니다");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.register(new User(user.getName(), user.getEmail(), user.getPassword()));
        model.addAttribute("loginRequest", new User());
        model.addAttribute("registerSuccess", true);
        return "login";
    }
}
