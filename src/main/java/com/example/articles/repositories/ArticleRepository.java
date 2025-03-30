package com.example.articles.repositories;

import com.example.articles.entities.Article;
import com.example.articles.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {


    List<Article> findByTitle(String title);


    List<Article> findByTitleContainingIgnoreCase(String titlePart);


    List<Article> findByTags_Name(String tagName);


    List<Article> findByAuthor(User author);


    List<Article> findByAuthor_Id(Long authorId);


    List<Article> findByTags_Id(Long tagId);
}
