package com.example.articles.controllers;

import com.example.articles.entities.User;
import com.example.articles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
private final UserService userService;

@Autowired
    public UserController(UserService userService) {
    this.userService = userService;
}
@GetMapping
    public String getAllUser(Model model) {
    List<User> users = userService.getAllUsers();
    model.addAttribute("users", users);
    return "users/list";
}


@GetMapping("/new")
    public String ShowCreateForm(Model model) {
    model.addAttribute("user", new User());
    return "users/add";
}
@PostMapping("/edit/{id}")
    public String ShowUpdateForm(@PathVariable UUID id, Model model) {
    Optional<User> user = userService.getUserById(id);
    user.ifPresent(value -> model.addAttribute("user", value));
    return "users/edit";
}

@PostMapping("/update/{id}")
    public String updateUser(@PathVariable UUID id, @ModelAttribute User user) {
    userService.updateUser(id, user);
    return "redirect:/user";
}
@PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return "redirect:/users";
}
}
