package com.example.articles.config;

import com.example.articles.entities.Article;
import com.example.articles.entities.ArticleComment;
import com.example.articles.entities.ArticleFavorite;
import com.example.articles.entities.Tag;
import com.example.articles.entities.User;
import com.example.articles.repositories.ArticleCommentRepository;
import com.example.articles.repositories.ArticleFavoriteRepository;
import com.example.articles.repositories.ArticleRepository;
import com.example.articles.repositories.TagRepository;
import com.example.articles.repositories.UserRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class InsertData {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;

    public InsertData(UserRepository userRepository,
                      TagRepository tagRepository,
                      ArticleRepository articleRepository,
                      ArticleCommentRepository articleCommentRepository,
                      ArticleFavoriteRepository articleFavoriteRepository) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.articleFavoriteRepository = articleFavoriteRepository;
    }

    @PostConstruct
    public void init() {
        Faker faker = new Faker(Locale.forLanguageTag("en"));

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User(); // вызывается конструктор без аргументов
            setField(user, "createdAt", LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            setField(user, "email", faker.internet().emailAddress());
            setField(user, "username", faker.name().username());
            setField(user, "imageUrl", faker.internet().avatar());
            setField(user, "password", faker.internet().password());
            setField(user, "bio", faker.lorem().sentence());
            setField(user, "articles", new ArrayList<>());
            setField(user, "comments", new ArrayList<>());
            setField(user, "favorites", new ArrayList<>());
            users.add(user);
        }
        users = userRepository.saveAll(users);

        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tag tag = new Tag();
            setField(tag, "createdAt", LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            setField(tag, "name", faker.book().genre());
            setField(tag, "articles", new HashSet<>());
            tags.add(tag);
        }
        tags = tagRepository.saveAll(tags);

        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            setField(article, "createdAt", LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            setField(article, "updatedAt", LocalDateTime.now());
            setField(article, "description", faker.lorem().sentence());
            setField(article, "slug", faker.internet().slug());
            setField(article, "title", faker.book().title());
            setField(article, "body", faker.lorem().paragraph(3));
            setField(article, "author", users.get(faker.number().numberBetween(0, users.size())));
            setField(article, "tags", new HashSet<>());
            setField(article, "comments", new ArrayList<>());
            setField(article, "favorites", new ArrayList<>());

            int numTags = faker.number().numberBetween(1, 3);
            @SuppressWarnings("unchecked")
            Set<Tag> articleTags = (Set<Tag>) getField(article, "tags");
            for (int j = 0; j < numTags; j++) {
                Tag randomTag = tags.get(faker.number().numberBetween(0, tags.size()));
                articleTags.add(randomTag);
            }
            articles.add(article);
        }
        articles = articleRepository.saveAll(articles);

        List<ArticleComment> comments = new ArrayList<>();
        for (Article article : articles) {
            int commentCount = faker.number().numberBetween(1, 6);
            for (int i = 0; i < commentCount; i++) {
                ArticleComment comment = new ArticleComment();
                setField(comment, "createdAt", LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                setField(comment, "updatedAt", LocalDateTime.now());
                setField(comment, "body", faker.lorem().sentence());
                setField(comment, "article", article);
                setField(comment, "user", users.get(faker.number().numberBetween(0, users.size())));
                comments.add(comment);
            }
        }
        articleCommentRepository.saveAll(comments);

        List<ArticleFavorite> favorites = new ArrayList<>();
        for (Article article : articles) {
            int favCount = faker.number().numberBetween(0, 4);
            for (int i = 0; i < favCount; i++) {
                ArticleFavorite favorite = new ArticleFavorite();
                setField(favorite, "createdAt", LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                setField(favorite, "updatedAt", LocalDateTime.now());
                setField(favorite, "article", article);
                setField(favorite, "user", users.get(faker.number().numberBetween(0, users.size())));
                favorites.add(favorite);
            }
        }
        articleFavoriteRepository.saveAll(favorites);
    }

    private <T> void setField(T object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка при установке поля " + fieldName + " для " + object.getClass().getName(), e);
        }
    }

    private Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка при получении поля " + fieldName + " для " + object.getClass().getName(), e);
        }
    }
}
