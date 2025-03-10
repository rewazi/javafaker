package com.example.articles.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Какая статья «лайкнута»/добавлена в избранное
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    // Какой пользователь добавил статью в избранное
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "ArticleFavorite{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", articleId=" + (article != null ? article.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}
