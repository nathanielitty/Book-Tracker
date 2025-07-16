package com.nathaniel.bookbackend.library.controller;

import com.nathaniel.bookbackend.library.model.UserBook;
import com.nathaniel.bookbackend.library.model.ReadingStatus;
import com.nathaniel.bookbackend.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/users/{userId}/books")
    public ResponseEntity<Page<UserBook>> getUserBooks(
            @PathVariable String userId,
            @RequestParam(required = false) ReadingStatus status,
            Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String tokenUserId = extractUserIdFromToken(authHeader);
        logger.info("[getUserBooks] Path userId: {} | Token userId: {} | Status: {}", userId, tokenUserId, status);
        if (tokenUserId == null) {
            logger.warn("[getUserBooks] No userId extracted from token. authHeader: {}", authHeader);
        }
        if (status != null) {
            return ResponseEntity.ok(libraryService.getUserBooksByStatus(userId, status, pageable));
        }
        return ResponseEntity.ok(libraryService.getUserBooks(userId, pageable));
    }

    @PostMapping("/users/{userId}/books/{bookId}")
    public ResponseEntity<UserBook> addBookToLibrary(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam ReadingStatus status,
            @RequestHeader("Authorization") String authHeader) {
        
        // Validate that the user can only access their own library
        String tokenUserId = extractUserIdFromToken(authHeader);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(libraryService.addBookToLibrary(userId, bookId, status));
    }

    @PutMapping("/users/{userId}/books/{bookId}/status")
    public ResponseEntity<UserBook> updateReadingStatus(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam ReadingStatus status,
            @RequestHeader("Authorization") String authHeader) {
        
        // Validate that the user can only access their own library
        String tokenUserId = extractUserIdFromToken(authHeader);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        return libraryService.updateReadingStatus(userId, bookId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{userId}/books/{bookId}/progress")
    public ResponseEntity<UserBook> updateReadingProgress(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam Integer currentPage,
            @RequestParam Integer totalPages,
            @RequestHeader("Authorization") String authHeader) {
        
        // Validate that the user can only access their own library
        String tokenUserId = extractUserIdFromToken(authHeader);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        return libraryService.updateReadingProgress(userId, bookId, currentPage, totalPages)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{userId}/books/{bookId}/review")
    public ResponseEntity<UserBook> addReviewAndRating(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam(required = false) String review,
            @RequestParam(required = false) Double rating,
            @RequestHeader("Authorization") String authHeader) {
        
        // Validate that the user can only access their own library
        String tokenUserId = extractUserIdFromToken(authHeader);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        return libraryService.addReviewAndRating(userId, bookId, review, rating)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{userId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromLibrary(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestHeader("Authorization") String authHeader) {
        
        // Validate that the user can only access their own library
        String tokenUserId = extractUserIdFromToken(authHeader);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        libraryService.removeBookFromLibrary(userId, bookId);
        return ResponseEntity.ok().build();
    }

    // Endpoint for frontend compatibility - shelf-based access
    @GetMapping("/{shelf}")
    public ResponseEntity<Page<UserBook>> getBooksByShelf(
            @PathVariable String shelf,
            @RequestHeader("Authorization") String authHeader,
            Pageable pageable) {
        // Extract user ID from token
        String userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            ReadingStatus status = ReadingStatus.valueOf(shelf.toUpperCase());
            return ResponseEntity.ok(libraryService.getUserBooksByStatus(userId, status, pageable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String extractUserIdFromToken(String authHeader) {
        // Extract token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("[extractUserIdFromToken] Invalid or missing Authorization header: {}", authHeader);
            return null;
        }
        
        String token = authHeader.substring(7);
        
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = parts[1];
                // Add padding if needed
                int padding = payload.length() % 4;
                if (padding > 0) {
                    payload += "====".substring(padding);
                }
                
                byte[] decoded = java.util.Base64.getDecoder().decode(payload);
                String json = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
                logger.info("[extractUserIdFromToken] JWT payload: {}", json);
                
                // Extract subject from JSON (simple approach)
                if (json.contains("\"sub\":")) {
                    int start = json.indexOf("\"sub\":\"") + 7;
                    int end = json.indexOf("\"", start);
                    if (start > 6 && end > start) {
                        String sub = json.substring(start, end);
                        logger.info("[extractUserIdFromToken] Extracted sub: {}", sub);
                        return sub;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[extractUserIdFromToken] Exception decoding token", e);
        }
        return null;
    }
}
