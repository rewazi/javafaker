package com.example.articles.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private String email;
    private String username;
    private String imageUrl;
    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // Убрали или не используем в данном контексте mappedBy="author"
    // Пускай User будет для комментариев и favorites
    // т.е. User НЕ знает о статьях, это делает Author.
    // @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Article> articles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleFavorite> favorites = new ArrayList<>();

    // Getters / Setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public List<ArticleComment> getComments() {
        return comments;
    }

    public List<ArticleFavorite> getFavorites() {
        return favorites;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setComments(List<ArticleComment> comments) {
        this.comments = comments;
    }

    public void setFavorites(List<ArticleFavorite> favorites) {
        this.favorites = favorites;
    }
}
