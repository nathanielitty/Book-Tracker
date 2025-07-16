package com.nathaniel.bookbackend.repository;

import com.nathaniel.bookbackend.models.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserIdOrderByScoreDesc(UUID userId);
    void deleteByUserId(UUID userId);
}