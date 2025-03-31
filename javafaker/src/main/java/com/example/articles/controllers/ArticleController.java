package com.example.articles.controllers;

import com.example.articles.entities.*;
import com.example.articles.repositories.ArticleCommentRepository;
import com.example.articles.repositories.UserRepository;
import com.example.articles.roles.Role;
import com.example.articles.service.ArticleService;
import com.example.articles.service.AuthorService;
import com.example.articles.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final UserRepository userRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Autowired
    public ArticleController(ArticleService articleService,
                             AuthorService authorService,
                             TagService tagService,
                             UserRepository userRepository,
                             ArticleCommentRepository articleCommentRepository) {
        this.articleService = articleService;
        this.authorService = authorService;
        this.tagService = tagService;
        this.userRepository = userRepository;
        this.articleCommentRepository = articleCommentRepository;
    }


    @GetMapping
    public String listArticles(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("currentUser", null);
        } else {

            User currentUser = userRepository.findByUsername(auth.getName());
            model.addAttribute("currentUser", currentUser);
        }
        return "articles/list";
    }



    @GetMapping("/{id}")
    public String articleDetails(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        model.addAttribute("article", article);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            currentUser = userRepository.findByUsername(auth.getName());
        }
        model.addAttribute("currentUser", currentUser);

        return "articles/details";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            currentUser = userRepository.findByUsername(auth.getName());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("article", new Article());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/add";
    }


    @PostMapping
    public String createArticle(@ModelAttribute("article") Article article,
                                @RequestParam("tagIds") List<Long> tagIds) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);


        article.setOwner(currentUser);


        article.setAuthor(currentUser);


        Set<Tag> tags = new HashSet<>();
        for (Long tagId : tagIds) {
            Tag tag = tagService.getTagById(tagId);
            tags.add(tag);
        }
        article.setTags(tags);


        articleService.createArticle(article);
        return "redirect:/articles";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        if (!currentUser.getRole().equals(Role.ADMIN_ROLE) &&
                (article.getOwner() == null || !article.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }

        model.addAttribute("article", article);
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/edit";
    }

    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable Long id,
                                @ModelAttribute("article") Article updatedArticle,
                                @RequestParam("authorId") Long authorId,
                                @RequestParam("tagIds") List<Long> tagIds) {

        Article existingArticle = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        if (!currentUser.getRole().equals(Role.ADMIN_ROLE) &&
                (existingArticle.getOwner() == null || !existingArticle.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }


        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + authorId));
        updatedArticle.setAuthor(author);

        Set<Tag> tags = new HashSet<>();
        for (Long tagId : tagIds) {
            Tag tag = tagService.getTagById(tagId);
            tags.add(tag);
        }
        updatedArticle.setTags(tags);


        updatedArticle.setOwner(existingArticle.getOwner());


        articleService.updateArticle(id, updatedArticle);
        return "redirect:/articles";
    }


    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        if (!currentUser.getRole().equals(Role.ADMIN_ROLE) &&
                (article.getOwner() == null || !article.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }

        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam("body") String body) {

        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }
        User currentUser = userRepository.findByUsername(auth.getName());

        // Создаем новый комментарий
        ArticleComment comment = new ArticleComment();
        comment.setBody(body);
        comment.setArticle(article);
        comment.setUser(currentUser);
        comment.setCreatedAt(LocalDateTime.now().withSecond(0).withNano(0));
        comment.setUpdatedAt(LocalDateTime.now().withSecond(0).withNano(0));


        articleCommentRepository.save(comment);


        return "redirect:/articles/" + id;
    }

    @GetMapping("/search")
    public String searchArticles(@RequestParam("q") String query, Model model) {
        List<Article> articles = articleService.searchArticles(query);
        model.addAttribute("articles", articles);
        return "articles/list";
    }


}
