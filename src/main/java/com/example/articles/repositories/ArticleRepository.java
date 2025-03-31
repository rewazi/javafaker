package com.example.articles.repositories;

import com.example.articles.entities.Article;
import com.example.articles.entities.Author;
import com.example.articles.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Поиск статьи по полному названию
    List<Article> findByTitle(String title);

    // Поиск статей, в названии которых содержится указанный отрывок (без учета регистра)
    List<Article> findByTitleContainingIgnoreCase(String titlePart);

    // Поиск статей по части заголовка или содержимого (без учета регистра)
    @Query("SELECT a FROM Article a WHERE lower(a.title) LIKE lower(concat('%', :q, '%')) OR lower(a.body) LIKE lower(concat('%', :q, '%'))")
    List<Article> searchArticles(@Param("q") String query);



    @Query("SELECT a FROM Author a WHERE lower(a.name) LIKE lower(concat('%', :q, '%')) ")
    List<Author> searchAuthor(@Param("q") String query);


}
