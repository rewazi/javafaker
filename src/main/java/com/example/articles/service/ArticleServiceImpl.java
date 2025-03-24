package com.example.articles.service;

import com.example.articles.entities.Article;
import com.example.articles.entities.Author;
import com.example.articles.entities.Tag;
import com.example.articles.repositories.ArticleRepository;
import com.example.articles.repositories.AuthorRepository;
import com.example.articles.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository,
                              AuthorRepository authorRepository,
                              TagRepository tagRepository) {
        this.articleRepository = articleRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }


    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }


    @Override
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }


    @Override
    public Article updateArticle(Long id, Article updatedArticle) {
        return articleRepository.findById(id)
                .map(article -> {
                    article.setTitle(updatedArticle.getTitle());
                    article.setDescription(updatedArticle.getDescription());
                    article.setSlug(updatedArticle.getSlug());
                    article.setBody(updatedArticle.getBody());

                    return articleRepository.save(article);
                })
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена, id=" + id));
    }


    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }


    @Override
    public List<Article> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthor_Id(authorId);
    }

    @Override
    public List<Article> getArticlesByTag(Long tagId) {
        return articleRepository.findByTags_Id(tagId);
    }


    @Override
    public List<Article> searchArticles(String query) {

        return articleRepository.findByTitleContainingIgnoreCase(query);
    }


    @Override
    public void createArticle(Article article, Long authorId, List<Long> tagIds) {

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Автор не найден (id=" + authorId + ")"));
        article.setAuthor(author);


        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> foundTags = tagRepository.findAllById(tagIds);
            article.setTags(new HashSet<>(foundTags));
        } else {
            article.setTags(new HashSet<>());
        }


        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());


        articleRepository.save(article);
    }


    @Override
    public void updateArticle(Long id, Article updatedArticle, Long authorId, List<Long> tagIds) {
        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена, id=" + id));


        existing.setTitle(updatedArticle.getTitle());
        existing.setDescription(updatedArticle.getDescription());
        existing.setBody(updatedArticle.getBody());
        existing.setUpdatedAt(LocalDateTime.now());


        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Автор не найден, id=" + authorId));
        existing.setAuthor(author);


        if (tagIds != null) {
            List<Tag> foundTags = tagRepository.findAllById(tagIds);
            existing.setTags(new HashSet<>(foundTags));
        } else {
            existing.setTags(new HashSet<>());
        }

        articleRepository.save(existing);
    }

}
