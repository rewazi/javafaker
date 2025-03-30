package com.example.articles.config;

import com.example.articles.entities.*;
import com.example.articles.repositories.*;
import com.example.articles.roles.Role;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class InsertData {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public InsertData(
            UserRepository userRepository,
            TagRepository tagRepository,
            ArticleRepository articleRepository,
            ArticleCommentRepository articleCommentRepository,
            ArticleFavoriteRepository articleFavoriteRepository
    ) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.articleFavoriteRepository = articleFavoriteRepository;
    }

    @PostConstruct
    public void init() {
        Faker faker = new Faker(Locale.forLanguageTag("en"));

        // 1. Создаём администратора, если его нет
        List<User> admins = userRepository.findByEmailContainingIgnoreCase("admin@example.com");
        if (admins.size() != 1) {
            createAdmin();
        } else {
            System.out.println("Админ с email admin@example.com уже существует");
        }

        // 2. Создаём 3 статических пользователя
        createUser("user1", "user1@example.com", "user123", Role.ROLE_USER);
        createUser("user2", "user2@example.com", "user123", Role.ROLE_USER);
        createUser("user3", "user3@example.com", "user123", Role.ROLE_USER);

        // 3. Генерируем дополнительных пользователей через Faker
        Set<String> usedEmails = new HashSet<>();
        List<User> fakerUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String email;
            do {
                email = faker.internet().emailAddress();
            } while (usedEmails.contains(email));
            usedEmails.add(email);

            User user = new User();
            user.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            user.setEmail(email);
            user.setUsername(faker.name().username());
            user.setImageUrl(faker.internet().avatar());
            String rawPassword = faker.internet().password();
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setBio(faker.lorem().sentence());
            user.setRole(Role.ROLE_USER);
            fakerUsers.add(user);
        }
        fakerUsers = userRepository.saveAll(fakerUsers);

        // Объединяем всех пользователей (статических + Faker)
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(userRepository.findAll());

        // 4. Генерируем теги через Faker
        List<Tag> fakerTags = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tag tag = new Tag();
            tag.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            tag.setName(faker.book().genre());
            fakerTags.add(tag);
        }
        fakerTags = tagRepository.saveAll(fakerTags);

        // 5. Создаём статьи (Article)
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            article.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            article.setUpdatedAt(LocalDateTime.now());
            article.setDescription(faker.lorem().sentence());
            article.setSlug(faker.internet().slug());
            article.setTitle(faker.book().title());
            article.setBody(faker.lorem().paragraph(3));

            // Устанавливаем автора статьи как случайного пользователя из allUsers
            User randomUser = allUsers.get(faker.number().numberBetween(0, allUsers.size()));
            article.setAuthor(randomUser);

            // Присваиваем случайные теги
            Set<Tag> articleTags = new HashSet<>();
            int numTags = faker.number().numberBetween(1, 3);
            for (int j = 0; j < numTags; j++) {
                Tag randomTag = fakerTags.get(faker.number().numberBetween(0, fakerTags.size()));
                articleTags.add(randomTag);
            }
            article.setTags(articleTags);

            articles.add(article);
        }
        articles = articleRepository.saveAll(articles);

        // 6. Создаём комментарии для статей
        List<ArticleComment> comments = new ArrayList<>();
        for (Article article : articles) {
            int commentCount = faker.number().numberBetween(1, 6);
            for (int i = 0; i < commentCount; i++) {
                ArticleComment comment = new ArticleComment();
                comment.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                comment.setUpdatedAt(LocalDateTime.now());
                comment.setBody(faker.lorem().sentence());
                comment.setArticle(article);

                User randomUser = allUsers.get(faker.number().numberBetween(0, allUsers.size()));
                comment.setUser(randomUser);

                comments.add(comment);
            }
        }
        articleCommentRepository.saveAll(comments);

        // 7. Создаём лайки для статей
        List<ArticleFavorite> favorites = new ArrayList<>();
        for (Article article : articles) {
            int favCount = faker.number().numberBetween(0, 4);
            for (int i = 0; i < favCount; i++) {
                ArticleFavorite favorite = new ArticleFavorite();
                favorite.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                favorite.setUpdatedAt(LocalDateTime.now());
                favorite.setArticle(article);

                User randomUser = allUsers.get(faker.number().numberBetween(0, allUsers.size()));
                favorite.setUser(randomUser);

                favorites.add(favorite);
            }
        }
        articleFavoriteRepository.saveAll(favorites);
    }

    private void createAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setBio("Я администратор");
        userRepository.save(admin);
    }

    private void createUser(String username, String email, String rawPassword, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setBio("Пользователь " + username);
        userRepository.save(user);
    }
}
