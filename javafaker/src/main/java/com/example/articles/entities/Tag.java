package com.example.articles.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private String name;

    // Двусторонняя связь с Article (через article_tag)
    @ManyToMany(mappedBy = "tags")
    private Set<Article> articles = new HashSet<>();

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", name='" + name + '\'' +
                '}';
    }
}
