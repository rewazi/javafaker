package com.example.articles.controllers;

import com.example.articles.entities.Article;
import com.example.articles.entities.User;
import com.example.articles.service.ArticleService;
import com.example.articles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public ArticleController(ArticleService articleService,
                             UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    // Список статей
    @GetMapping
    public String getAllArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "articles/list";
    }

    // Детали статьи
    @GetMapping("/{id}")
    public String getArticleById(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена (id=" + id + ")"));
        model.addAttribute("article", article);
        return "articles/details";
    }

    // Форма создания статьи
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        return "articles/add"; // шаблон add.html
    }

    // Добавление статьи
    @PostMapping
    public String createArticle(@ModelAttribute Article article) {
        // Получаем текущего пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Предполагаем, что наш CustomUserDetails имеет метод getId()
        Long currentUserId = ((com.example.articles.details.CustomUserDetails) auth.getPrincipal()).getId();
        User currentUser = userService.getUserById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        // Устанавливаем автора статьи как текущего пользователя
        article.setAuthor(currentUser);
        articleService.createArticle(article);
        return "redirect:/articles";
    }

    // Форма редактирования статьи
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена (id=" + id + ")"));
        // Получаем текущего пользователя
        Long currentUserId = ((com.example.articles.details.CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        // Если текущий пользователь не является автором, запрещаем редактирование
        if (!article.getAuthor().getId().equals(currentUserId)) {
            return "redirect:/articles?error=accessDenied";
        }
        model.addAttribute("article", article);
        return "articles/edit"; // шаблон edit.html
    }

    // Сохранение изменений статьи
    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable Long id, @ModelAttribute Article updatedArticle) {
        // Получаем текущего пользователя
        Long currentUserId = ((com.example.articles.details.CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Article existingArticle = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена (id=" + id + ")"));
        if (!existingArticle.getAuthor().getId().equals(currentUserId)) {
            return "redirect:/articles?error=accessDenied";
        }
        // Обновляем поля статьи (без смены автора)
        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setDescription(updatedArticle.getDescription());
        existingArticle.setSlug(updatedArticle.getSlug());
        existingArticle.setBody(updatedArticle.getBody());
        // (Если нужно обновлять теги или другие связи – добавить соответствующую логику)
        articleService.updateArticle(id, existingArticle);
        return "redirect:/articles";
    }

    // Удаление статьи
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        // Получаем текущего пользователя
        Long currentUserId = ((com.example.articles.details.CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена (id=" + id + ")"));
        if (!article.getAuthor().getId().equals(currentUserId)) {
            return "redirect:/articles?error=accessDenied";
        }
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    // Поиск, по тегу и по автору можно оставить без изменений (при необходимости добавить проверки)
}
