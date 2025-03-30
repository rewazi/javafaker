package com.example.articles.service;

import com.example.articles.entities.Article;
import com.example.articles.entities.User;
import com.example.articles.repositories.ArticleRepository;
import com.example.articles.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * Создаёт новую статью, устанавливая текущего пользователя (из SecurityContext)
     * в качестве автора.
     */
    @Override
    public Article createArticle(Article article) {
        User currentUser = getCurrentUser();
        article.setAuthor(currentUser);
        return articleRepository.save(article);
    }

    /**
     * Обновляет статью, если текущий пользователь является её автором.
     */
    @Override
    public Article updateArticle(Long id, Article updatedArticle) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена с id " + id));
        User currentUser = getCurrentUser();
        if (!article.getAuthor().getUsername().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("У вас нет прав для редактирования этой статьи");
        }
        // Обновляем поля (автора менять не будем)
        article.setTitle(updatedArticle.getTitle());
        article.setDescription(updatedArticle.getDescription());
        article.setSlug(updatedArticle.getSlug());
        article.setBody(updatedArticle.getBody());
        article.setTags(updatedArticle.getTags());
        return articleRepository.save(article);
    }

    /**
     * Удаляет статью, если текущий пользователь является её автором.
     */
    @Override
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена с id " + id));
        User currentUser = getCurrentUser();
        if (!article.getAuthor().getUsername().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("У вас нет прав для удаления этой статьи");
        }
        articleRepository.delete(article);
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
        // Простой поиск по заголовку, описанию и содержимому
        return articleRepository.findAll().stream()
                .filter(article -> article.getTitle().toLowerCase().contains(query.toLowerCase())
                        || article.getDescription().toLowerCase().contains(query.toLowerCase())
                        || article.getBody().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Если нужны дополнительные методы с передачей id автора и списка id тегов,
    // их можно реализовать аналогичным образом. Здесь оставляем пустую реализацию:
    @Override
    public void updateArticle(Long id, Article article, Long authorId, List<Long> tagIds) {
        // Реализуйте при необходимости
    }

    @Override
    public void createArticle(Article article, Long authorId, List<Long> tagIds) {
        // Реализуйте при необходимости
    }

    /**
     * Вспомогательный метод для получения текущего пользователя из SecurityContext
     */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден с username " + username));
    }
}
