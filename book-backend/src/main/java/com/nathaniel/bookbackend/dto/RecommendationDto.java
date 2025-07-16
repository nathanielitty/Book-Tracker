package com.nathaniel.bookbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RecommendationDto {
    private String externalId;
    private String title;
    private String thumbnailUrl;
    private Double score;
    private LocalDateTime computedAt;
}