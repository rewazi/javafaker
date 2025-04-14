package com.example.articles.repositories;

import com.example.articles.entities.Article;
import com.example.articles.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {





    @Query("SELECT  article FROM Article article " +
            "LEFT JOIN article.author author " +
            "LEFT JOIN article.tags tag " +
            "WHERE LOWER(article.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(article.body) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(author.username) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(tag.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY article.createdAt DESC")
    List<Article> searchAll(@Param("q") String query);

    List<Article> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    List<Article> findByTagsIdOrderByCreatedAtDesc(Long tagId);

    List<Article> findAllByOrderByCreatedAtDesc();

}