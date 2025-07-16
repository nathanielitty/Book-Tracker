package com.nathaniel.bookbackend.book.repository;

import com.nathaniel.bookbackend.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    Optional<Book> findByIsbn(String isbn);
    
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
        String title, String author, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE :genre MEMBER OF b.genres")
    Page<Book> findByGenre(String genre, Pageable pageable);
    
    @Query("SELECT DISTINCT genre FROM Book b JOIN b.genres genre")
    List<String> findAllGenres();
    
    List<Book> findByAverageRatingGreaterThanEqual(Double rating);
}
