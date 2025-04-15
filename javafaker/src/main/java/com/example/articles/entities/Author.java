package com.example.articles.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Сущность «Автор» (случайно сгенерированный автор статей).
 */
@Entity
@Table(name = "author") // Имя таблицы можно оставить, либо поменять, напр. на "randomly_generated_authors"
public class Author {

    /**
     * Уникальный идентификатор случайно сгенерированного автора статей
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя случайно сгенерированного автора статей (обязательно)
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Краткая биография случайно сгенерированного автора (необязательно)
     */
    @Column(length = 255)
    private String bio;

    /**
     * Дата и время создания записи (проставляется автоматически)
     */
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи (обновляется автоматически)
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Author() {
    }

    // ===== Геттеры и Сеттеры =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
