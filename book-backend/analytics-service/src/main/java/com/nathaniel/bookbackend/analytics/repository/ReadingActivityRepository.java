package com.nathaniel.bookbackend.analytics.repository;

import com.nathaniel.bookbackend.analytics.model.ReadingActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReadingActivityRepository extends JpaRepository<ReadingActivity, String> {
    Page<ReadingActivity> findByUserIdOrderByActivityDateDesc(String userId, Pageable pageable);
    
    List<ReadingActivity> findByUserIdAndActivityDateBetween(String userId, Instant start, Instant end);
    
    @Query("SELECT SUM(r.pagesRead) FROM ReadingActivity r WHERE r.userId = ?1 AND r.activityDate BETWEEN ?2 AND ?3")
    Integer getTotalPagesReadBetween(String userId, Instant start, Instant end);
    
    @Query("SELECT SUM(r.timeSpent) FROM ReadingActivity r WHERE r.userId = ?1 AND r.activityDate BETWEEN ?2 AND ?3")
    Double getTotalTimeSpentBetween(String userId, Instant start, Instant end);
}
