package com.nathaniel.bookbackend.library.controller;

import com.nathaniel.bookbackend.library.model.UserBook;
import com.nathaniel.bookbackend.library.model.ReadingStatus;
import com.nathaniel.bookbackend.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/users/{userId}/books")
    public ResponseEntity<Page<UserBook>> getUserBooks(
            @PathVariable String userId,
            @RequestParam(required = false) ReadingStatus status,
            Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(libraryService.getUserBooksByStatus(userId, status, pageable));
        }
        return ResponseEntity.ok(libraryService.getUserBooks(userId, pageable));
    }

    @PostMapping("/users/{userId}/books/{bookId}")
    public ResponseEntity<UserBook> addBookToLibrary(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam ReadingStatus status) {
        return ResponseEntity.ok(libraryService.addBookToLibrary(userId, bookId, status));
    }

    @PutMapping("/users/{userId}/books/{bookId}/status")
    public ResponseEntity<UserBook> updateReadingStatus(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam ReadingStatus status) {
        return libraryService.updateReadingStatus(userId, bookId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{userId}/books/{bookId}/progress")
    public ResponseEntity<UserBook> updateReadingProgress(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam Integer currentPage,
            @RequestParam Integer totalPages) {
        return libraryService.updateReadingProgress(userId, bookId, currentPage, totalPages)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{userId}/books/{bookId}/review")
    public ResponseEntity<UserBook> addReviewAndRating(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam(required = false) String review,
            @RequestParam(required = false) Double rating) {
        return libraryService.addReviewAndRating(userId, bookId, review, rating)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{userId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromLibrary(
            @PathVariable String userId,
            @PathVariable String bookId) {
        libraryService.removeBookFromLibrary(userId, bookId);
        return ResponseEntity.ok().build();
    }

    // Endpoint for frontend compatibility - shelf-based access
    @GetMapping("/{shelf}")
    public ResponseEntity<Page<UserBook>> getBooksByShelf(
            @PathVariable String shelf,
            @RequestHeader("Authorization") String authHeader,
            Pageable pageable) {
        // Extract user ID from token (you'll need to implement this)
        // For now, return empty list or implement user extraction
        String userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
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
            return null;
        }
        
        String token = authHeader.substring(7);
        
        // For now, just decode the token without validation
        // In production, you'd want to validate with auth service
        return decodeTokenSubject(token);
    }
    
    private String decodeTokenSubject(String token) {
        // Simple base64 decoding of JWT payload (not secure, for demo purposes)
        // In production, use proper JWT library
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
                
                // Extract subject from JSON (simple approach)
                if (json.contains("\"sub\":")) {
                    int start = json.indexOf("\"sub\":\"") + 7;
                    int end = json.indexOf("\"", start);
                    if (start > 6 && end > start) {
                        return json.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            // Decoding failed
        }
        return null;
    }
}
