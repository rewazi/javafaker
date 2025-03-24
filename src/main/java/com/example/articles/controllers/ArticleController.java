package com.example.articles.controllers;

import com.example.articles.entities.Article;
import com.example.articles.service.ArticleService;
import com.example.articles.service.AuthorService;
import com.example.articles.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final AuthorService authorService;
    private final TagService tagService;

    @Autowired
    public ArticleController(
            ArticleService articleService,
            AuthorService authorService,
            TagService tagService
    ) {
        this.articleService = articleService;
        this.authorService = authorService;
        this.tagService = tagService;
    }


    @GetMapping
    public String getAllArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "articles/list";
    }


    @GetMapping("/{id}")
    public String getArticleById(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена (id=" + id + ")"));
        model.addAttribute("article", article);
        return "articles/details";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/add"; // add.html
    }

    @PostMapping
    public String createArticle(
            @ModelAttribute Article article,
            @RequestParam Long authorId,
            @RequestParam(required = false) List<Long> tagIds
    ) {
        articleService.createArticle(article, authorId, tagIds);  // <-- теперь реализован
        return "redirect:/articles";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена (id=" + id + ")"));
        model.addAttribute("article", article);
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/edit"; // edit.html
    }

    @PostMapping("/update/{id}")
    public String updateArticle(
            @PathVariable Long id,
            @ModelAttribute Article article,
            @RequestParam Long authorId,
            @RequestParam(required = false) List<Long> tagIds
    ) {
        articleService.updateArticle(id, article, authorId, tagIds);
        return "redirect:/articles";
    }


    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }


    @GetMapping("/by-author/{authorId}")
    public String getArticlesByAuthor(@PathVariable Long authorId, Model model) {
        model.addAttribute("articles", articleService.getArticlesByAuthor(authorId));
        return "articles/list";
    }


    @GetMapping("/by-tag/{tagId}")
    public String getArticlesByTag(@PathVariable Long tagId, Model model) {
        model.addAttribute("articles", articleService.getArticlesByTag(tagId));
        return "articles/list";
    }


    @GetMapping("/search")
    public String searchArticles(@RequestParam String query, Model model) {
        model.addAttribute("articles", articleService.searchArticles(query));
        return "articles/list";
    }
}
