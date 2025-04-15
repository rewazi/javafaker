package com.example.articles.controllers;

import com.example.articles.entities.User;
import com.example.articles.roles.Role;
import com.example.articles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute User user, Model model) {


        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            user.setEmail(user.getUsername() + "@example.com");
        }


        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Пользователь с таким логином уже существует!");
            model.addAttribute("user", user);
            return "registration";
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Пользователь с таким email уже существует!");
            model.addAttribute("user", user);
            return "registration";
        }


        user.setRole(Role.USER_ROLE);
        user.setCreatedAt(LocalDateTime.now());
        if (user.getImageUrl() == null || user.getImageUrl().trim().isEmpty()) {
            user.setImageUrl("https://via.placeholder.com/150");
        }
        if (user.getBio() == null) {
            user.setBio("");
        }

        // Создаём пользователя
        userService.createUser(user);
        return "redirect:/login";
    }
}