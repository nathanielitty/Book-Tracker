package com.nathaniel.bookbackend.library.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_books")
public class UserBook {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String bookId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    private Integer currentPage;

    private Integer totalPages;

    private Double rating;

    @Column(length = 2000)
    private String review;

    private Instant startedAt;
    private Instant finishedAt;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public ReadingStatus getStatus() { return status; }
    public void setStatus(ReadingStatus status) { this.status = status; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
