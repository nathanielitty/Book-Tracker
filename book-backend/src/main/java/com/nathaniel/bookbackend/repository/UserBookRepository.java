package com.nathaniel.bookbackend.repository;

import com.nathaniel.bookbackend.models.Shelf;
import com.nathaniel.bookbackend.models.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findByUserIdAndShelf(UUID userId, Shelf shelf);
    List<UserBook> findByUserId(UUID userId);
}