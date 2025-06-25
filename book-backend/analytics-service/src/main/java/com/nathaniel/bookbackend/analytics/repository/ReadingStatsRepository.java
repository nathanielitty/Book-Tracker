package com.nathaniel.bookbackend.analytics.repository;

import com.nathaniel.bookbackend.analytics.model.ReadingStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingStatsRepository extends JpaRepository<ReadingStats, String> {
}
