package com.nathaniel.bookbackend.library.service;

import com.nathaniel.bookbackend.library.model.UserBook;
import com.nathaniel.bookbackend.library.model.ReadingStatus;
import com.nathaniel.bookbackend.library.repository.UserBookRepository;
import com.nathaniel.bookbackend.common.events.UserActivityEvent;
import com.nathaniel.bookbackend.common.events.KafkaTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class LibraryService {
    
    @Autowired
    private UserBookRepository userBookRepository;
    
    @Autowired
    private KafkaTemplate<String, UserActivityEvent> kafkaTemplate;

    @Transactional(readOnly = true)
    public Page<UserBook> getUserBooks(String userId, Pageable pageable) {
        return userBookRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserBook> getUserBooksByStatus(String userId, ReadingStatus status, Pageable pageable) {
        return userBookRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Transactional
    public UserBook addBookToLibrary(String userId, String bookId, ReadingStatus status) {
        UserBook userBook = new UserBook();
        userBook.setUserId(userId);
        userBook.setBookId(bookId);
        userBook.setStatus(status);
        
        if (status == ReadingStatus.CURRENTLY_READING) {
            userBook.setStartedAt(Instant.now());
        }
        
        UserBook savedBook = userBookRepository.save(userBook);
        
        // Publish event
        kafkaTemplate.send(KafkaTopics.USER_ACTIVITY, new UserActivityEvent(
            userId,
            "BOOK_ADDED",
            bookId
        ));
        
        return savedBook;
    }

    @Transactional
    public Optional<UserBook> updateReadingStatus(String userId, String bookId, ReadingStatus newStatus) {
        return userBookRepository.findByUserIdAndBookId(userId, bookId)
            .map(userBook -> {
                ReadingStatus oldStatus = userBook.getStatus();
                userBook.setStatus(newStatus);
                
                if (newStatus == ReadingStatus.CURRENTLY_READING && oldStatus != ReadingStatus.CURRENTLY_READING) {
                    userBook.setStartedAt(Instant.now());
                } else if (newStatus == ReadingStatus.READ) {
                    userBook.setFinishedAt(Instant.now());
                }
                
                UserBook updatedBook = userBookRepository.save(userBook);
                
                // Publish event
                kafkaTemplate.send(KafkaTopics.USER_ACTIVITY, new UserActivityEvent(
                    userId,
                    "STATUS_UPDATED",
                    bookId
                ));
                
                return updatedBook;
            });
    }

    @Transactional
    public Optional<UserBook> updateReadingProgress(String userId, String bookId, Integer currentPage, Integer totalPages) {
        return userBookRepository.findByUserIdAndBookId(userId, bookId)
            .map(userBook -> {
                userBook.setCurrentPage(currentPage);
                userBook.setTotalPages(totalPages);
                
                if (currentPage.equals(totalPages)) {
                    userBook.setStatus(ReadingStatus.READ);
                    userBook.setFinishedAt(Instant.now());
                }
                
                UserBook updatedBook = userBookRepository.save(userBook);
                
                // Publish event
                kafkaTemplate.send(KafkaTopics.USER_ACTIVITY, new UserActivityEvent(
                    userId,
                    "PROGRESS_UPDATED",
                    bookId
                ));
                
                return updatedBook;
            });
    }

    @Transactional
    public Optional<UserBook> addReviewAndRating(String userId, String bookId, String review, Double rating) {
        return userBookRepository.findByUserIdAndBookId(userId, bookId)
            .map(userBook -> {
                userBook.setReview(review);
                userBook.setRating(rating);
                
                UserBook updatedBook = userBookRepository.save(userBook);
                
                // Publish event
                kafkaTemplate.send(KafkaTopics.USER_ACTIVITY, new UserActivityEvent(
                    userId,
                    "REVIEW_ADDED",
                    bookId
                ));
                
                return updatedBook;
            });
    }

    @Transactional
    public void removeBookFromLibrary(String userId, String bookId) {
        userBookRepository.deleteByUserIdAndBookId(userId, bookId);
        
        // Publish event
        kafkaTemplate.send(KafkaTopics.USER_ACTIVITY, new UserActivityEvent(
            userId,
            "BOOK_REMOVED",
            bookId
        ));
    }
}
