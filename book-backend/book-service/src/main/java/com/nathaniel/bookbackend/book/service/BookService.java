package com.nathaniel.bookbackend.book.service;

import com.nathaniel.bookbackend.book.model.Book;
import com.nathaniel.bookbackend.book.repository.BookRepository;
import com.nathaniel.bookbackend.events.BookUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private KafkaTemplate<String, BookUpdateEvent> kafkaTemplate;

    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            query, query, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Transactional
    public Book createBook(Book book) {
        Book savedBook = bookRepository.save(book);
        
        // Publish book creation event
        kafkaTemplate.send("book-updates", new BookUpdateEvent(
            savedBook.getId(),
            "CREATED",
            null
        ));
        
        return savedBook;
    }

    @Transactional
    public Optional<Book> updateBook(String id, Book bookDetails) {
        return bookRepository.findById(id)
            .map(existingBook -> {
                // Update fields
                existingBook.setTitle(bookDetails.getTitle());
                existingBook.setAuthor(bookDetails.getAuthor());
                existingBook.setDescription(bookDetails.getDescription());
                existingBook.setCoverUrl(bookDetails.getCoverUrl());
                existingBook.setGenres(bookDetails.getGenres());
                existingBook.setTags(bookDetails.getTags());
                existingBook.setPublishedYear(bookDetails.getPublishedYear());
                existingBook.setPublisher(bookDetails.getPublisher());
                existingBook.setPageCount(bookDetails.getPageCount());
                existingBook.setLanguage(bookDetails.getLanguage());
                
                Book updatedBook = bookRepository.save(existingBook);
                
                // Publish book update event
                kafkaTemplate.send("book-updates", new BookUpdateEvent(
                    updatedBook.getId(),
                    "UPDATED",
                    null
                ));
                
                return updatedBook;
            });
    }

    @Transactional
    public void deleteBook(String id) {
        bookRepository.deleteById(id);
        
        // Publish book deletion event
        kafkaTemplate.send("book-updates", new BookUpdateEvent(
            id,
            "DELETED",
            null
        ));
    }

    @Transactional(readOnly = true)
    public Page<Book> getBooksByGenre(String genre, Pageable pageable) {
        return bookRepository.findByGenre(genre, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getAllGenres() {
        return bookRepository.findAllGenres();
    }
}
