package com.nathaniel.bookbackend.library.repository;

import com.nathaniel.bookbackend.library.model.UserBook;
import com.nathaniel.bookbackend.library.model.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, String> {
    Page<UserBook> findByUserId(String userId, Pageable pageable);
    
    Page<UserBook> findByUserIdAndStatus(String userId, ReadingStatus status, Pageable pageable);
    
    Optional<UserBook> findByUserIdAndBookId(String userId, String bookId);
    
    List<UserBook> findByUserIdAndStatusOrderByStartedAtDesc(String userId, ReadingStatus status);
    
    boolean existsByUserIdAndBookId(String userId, String bookId);
    
    void deleteByUserIdAndBookId(String userId, String bookId);
}
