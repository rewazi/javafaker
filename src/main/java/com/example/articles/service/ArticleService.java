package com.example.articles.service;

import com.example.articles.entities.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleService {


    List<Article> getAllArticles();
    Optional<Article> getArticleById(Long id);
    Article createArticle(Article article); // если используется где-то
    Article updateArticle(Long id, Article updatedArticle); // если используется где-то
    void deleteArticle(Long id);
    List<Article> getArticlesByAuthor(Long authorId);
    List<Article> getArticlesByTag(Long tagId);
    List<Article> searchArticles(String query);


    void createArticle(Article article, Long authorId, List<Long> tagIds);
    void updateArticle(Long id, Article updatedArticle, Long authorId, List<Long> tagIds);
}
