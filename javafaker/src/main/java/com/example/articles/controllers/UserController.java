package com.example.articles.controllers;

import com.example.articles.entities.User;
import com.example.articles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
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

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "users/add";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, Model model) {
        // Сначала проверяем, существует ли уже пользователь с таким логином
        if (userService.findByUsername(user.getUsername()) != null) {
            // Если да — показываем ошибку и возвращаемся на форму
            model.addAttribute("error", "Пользователь с таким именем уже существует!");
            model.addAttribute("user", user);
            return "users/add";
        }



        userService.createUser(user);
        return "redirect:/users";

    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/edit";
        } else {
            // Если пользователь не найден — можно показать ошибку или перейти к списку
            return "redirect:/users";
        }
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User updatedUser,
                             Model model) {
        Optional<User> existing = userService.getUserById(id);

        if (existing.isEmpty()) {
            return "redirect:/users";
        }

        User currentUser = existing.get();
        // Если пользователь меняет логин, проверяем, не занят ли он другим
        if (!currentUser.getUsername().equals(updatedUser.getUsername())) {
            User userWithSameName = userService.findByUsername(updatedUser.getUsername());
            // Если логин уже у кого-то занят (и это не сам он), выдаём ошибку
            if (userWithSameName != null && !userWithSameName.getId().equals(id)) {
                model.addAttribute("error", "Логин уже используется другим пользователем!");
                model.addAttribute("user", updatedUser);
                return "users/edit";
            }
        }

        // Обновляем
        userService.updateUser(id, updatedUser);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}