package com.nathaniel.bookbackend.library;

import com.nathaniel.bookbackend.library.model.UserBook;
import com.nathaniel.bookbackend.library.model.ReadingStatus;
import com.nathaniel.bookbackend.library.service.LibraryService;
import com.nathaniel.bookbackend.library.repository.UserBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class LibraryServiceIntegrationTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserBookRepository userBookRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeEach
    void setUp() {
        userBookRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void shouldAddBookToLibrary() {
        // Given
        String userId = "test-user";
        String bookId = "test-book";
        ReadingStatus status = ReadingStatus.WANT_TO_READ;
        
        // When
        UserBook result = libraryService.addBookToLibrary(userId, bookId, status);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getStatus()).isEqualTo(ReadingStatus.WANT_TO_READ);
    }

    @Test
    void shouldUpdateReadingStatus() {
        // Given
        String userId = "test-user";
        String bookId = "test-book";
        libraryService.addBookToLibrary(userId, bookId, ReadingStatus.WANT_TO_READ);

        // When
        var result = libraryService.updateReadingStatus(userId, bookId, ReadingStatus.CURRENTLY_READING);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(ReadingStatus.CURRENTLY_READING);
        assertThat(result.get().getStartedAt()).isNotNull();
    }

    @Test
    void shouldUpdateReadingProgress() {
        // Given
        String userId = "test-user";
        String bookId = "test-book";
        libraryService.addBookToLibrary(userId, bookId, ReadingStatus.CURRENTLY_READING);

        // When
        var result = libraryService.updateReadingProgress(userId, bookId, 50, 100);

        // Then
        assertThat(result).isPresent();
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCurrentPage()).isEqualTo(50);
        assertThat(result.get().getTotalPages()).isEqualTo(100);
    }

    @Test
    void shouldAddReviewAndRating() {
        // Given
        String userId = "test-user";
        String bookId = "test-book";
        libraryService.addBookToLibrary(userId, bookId, ReadingStatus.READ);

        // When
        var result = libraryService.addReviewAndRating(userId, bookId, "Great book!", 4.5);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRating()).isEqualTo(4.5);
        assertThat(result.get().getReview()).isEqualTo("Great book!");
    }
}
