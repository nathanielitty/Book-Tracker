package com.nathaniel.bookbackend.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="recommendation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Double score;

    private LocalDateTime computedAt = LocalDateTime.now();
}
