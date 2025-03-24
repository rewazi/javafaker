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
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public InsertData(
            UserRepository userRepository,
            AuthorRepository authorRepository,
            TagRepository tagRepository,
            ArticleRepository articleRepository,
            ArticleCommentRepository articleCommentRepository,
            ArticleFavoriteRepository articleFavoriteRepository
    ) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.articleFavoriteRepository = articleFavoriteRepository;
    }

    @PostConstruct
    public void init() {

        Faker faker = new Faker(Locale.forLanguageTag("en"));

        // Check if the admin exists and remove duplicates if necessary
        List<User> admins = userRepository.findByEmailContainingIgnoreCase("admin@example.com");

        if (admins.size() > 1) {
            System.out.println("Найдено " + admins.size() + " записей c email admin@example.com. Удаляем дубли...");
            for (User dub : admins) {
                userRepository.delete(dub);
            }
            createAdmin();
        } else if (admins.size() == 1) {
            System.out.println("Админ с email admin@example.com уже есть, ничего не делаем");
        } else {
            createAdmin();
        }

        // Create 3 predefined users
        createUser("user1", "user1@example.com", "user123", Role.ROLE_USER);
        createUser("user2", "user2@example.com", "user123", Role.ROLE_USER);
        createUser("user3", "user3@example.com", "user123", Role.ROLE_USER);

        // Create authors
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Author author = new Author();
            author.setName(faker.name().fullName());
            author.setBio(faker.lorem().sentence());
            authors.add(author);
        }
        authors = authorRepository.saveAll(authors);

        // Create users using Faker
        createFakeUsers(faker);

        // Create tags
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tag tag = new Tag();
            tag.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            tag.setName(faker.book().genre());
            tags.add(tag);
        }
        tags = tagRepository.saveAll(tags);

        // Create articles
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            article.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
            article.setUpdatedAt(LocalDateTime.now());
            article.setDescription(faker.lorem().sentence());
            article.setSlug(faker.internet().slug());
            article.setTitle(faker.book().title());
            article.setBody(faker.lorem().paragraph(3));

            Author randomAuthor = authors.get(faker.number().numberBetween(0, authors.size()));
            article.setAuthor(randomAuthor);

            Set<Tag> articleTags = new HashSet<>();
            int numTags = faker.number().numberBetween(1, 3);
            for (int j = 0; j < numTags; j++) {
                Tag randomTag = tags.get(faker.number().numberBetween(0, tags.size()));
                articleTags.add(randomTag);
            }
            article.setTags(articleTags);

            articles.add(article);
        }
        articles = articleRepository.saveAll(articles);

        // Create comments and favorites for articles
        createCommentsAndFavorites(articles);
    }

    private void createAdmin() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setBio("I'm the admin user");
        userRepository.save(adminUser);
        System.out.println("Администратор admin@example.com (admin123) создан");
    }

    // Method to create predefined users
    private void createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setBio("I'm the " + username);
        userRepository.save(user);
        System.out.println(username + " created");
    }

    private void createFakeUsers(Faker faker) {
        Set<String> usedEmails = new HashSet<>();
        List<User> users = new ArrayList<>();
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

            users.add(user);
        }
        users = userRepository.saveAll(users);
        System.out.println("Фейковые пользователи созданы");
    }

    private void createCommentsAndFavorites(List<Article> articles) {
        Faker faker = new Faker(Locale.forLanguageTag("en"));
        List<ArticleComment> comments = new ArrayList<>();
        for (Article article : articles) {
            int commentCount = faker.number().numberBetween(1, 6);
            for (int i = 0; i < commentCount; i++) {
                ArticleComment comment = new ArticleComment();
                comment.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                comment.setUpdatedAt(LocalDateTime.now());
                comment.setBody(faker.lorem().sentence());
                comment.setArticle(article);

                // Choose a random user for the comment
                List<User> users = userRepository.findAll();
                User randomUser = users.get(faker.number().numberBetween(0, users.size()));
                comment.setUser(randomUser);

                comments.add(comment);
            }
        }
        articleCommentRepository.saveAll(comments);

        List<ArticleFavorite> favorites = new ArrayList<>();
        for (Article article : articles) {
            int favCount = faker.number().numberBetween(0, 4);
            for (int i = 0; i < favCount; i++) {
                ArticleFavorite favorite = new ArticleFavorite();
                favorite.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                favorite.setUpdatedAt(LocalDateTime.now());
                favorite.setArticle(article);

                // Choose a random user for the favorite
                List<User> users = userRepository.findAll();
                User randomUser = users.get(faker.number().numberBetween(0, users.size()));
                favorite.setUser(randomUser);

                favorites.add(favorite);
            }
        }
        articleFavoriteRepository.saveAll(favorites);
    }
}
