package com.example.articles.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static java.awt.SystemColor.text;

@Entity
@Table(name = "article_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String body;

    // К какой статье относится комментарий
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    // Какому пользователю принадлежит комментарий
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "ArticleComment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
