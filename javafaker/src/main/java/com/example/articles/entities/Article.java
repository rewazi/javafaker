package com.example.articles.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String slug;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleFavorite> favorites = new ArrayList<>();

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        String tagInfo;
        if (Hibernate.isInitialized(tags)) {
            tagInfo = String.valueOf(tags.size());
        } else {
            tagInfo = "не загружены";
        }
        return "Article{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", description='" + description + '\'' +
                ", slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", body='" + (body != null ? (body.length() > 30 ? body.substring(0, 30) + "..." : body) : null) + '\'' +
                ", authorId=" + (author != null ? author.getId() : null) +
                ", tagsCount=" + tagInfo +
                '}';
    }
}