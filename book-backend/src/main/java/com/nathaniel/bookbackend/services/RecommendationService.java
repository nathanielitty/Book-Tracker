package com.nathaniel.bookbackend.services;

import com.nathaniel.bookbackend.dto.RecommendationDto;
import com.nathaniel.bookbackend.models.AppUser;
import com.nathaniel.bookbackend.models.Book;
import com.nathaniel.bookbackend.models.UserBook;
import com.nathaniel.bookbackend.repository.AppUserRepository;
import com.nathaniel.bookbackend.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final AppUserRepository userRepository;
    private final UserBookRepository userBookRepository;

    public List<RecommendationDto> getRecommendations(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        UUID userId = user.getId();
        // Books user has interacted with
        Set<Long> excludedBookIds = userBookRepository.findByUserId(userId)
            .stream()
            .map(ub -> ub.getBook().getId())
            .collect(Collectors.toSet());
        // All ratings from other users
        List<UserBook> otherRatings = userBookRepository.findAll()
            .stream()
            .filter(ub -> ub.getRating() != null && !ub.getUser().getId().equals(userId))
            .collect(Collectors.toList());
        // Compute average rating per book
        Map<Book, Double> avgRatings = otherRatings.stream()
            .collect(Collectors.groupingBy(UserBook::getBook,
                Collectors.averagingDouble(UserBook::getRating)));
        // Create recommendation DTOs sorted by score desc
        return avgRatings.entrySet().stream()
            .filter(e -> !excludedBookIds.contains(e.getKey().getId()))
            .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
            .map(e -> {
                Book b = e.getKey();
                return new RecommendationDto(
                    b.getExternalId(),
                    b.getTitle(),
                    b.getThumbnailUrl(),
                    e.getValue(),
                    LocalDateTime.now()
                );
            })
            .collect(Collectors.toList());
    }
}