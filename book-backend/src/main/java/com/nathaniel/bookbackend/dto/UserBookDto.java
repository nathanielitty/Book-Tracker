package com.nathaniel.bookbackend.dto;

import com.nathaniel.bookbackend.models.Shelf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserBookDto {
    private Long id;
    private String externalId;
    private String title;
    private String thumbnailUrl;
    private String description;
    private Shelf shelf;
    private Integer rating;
    private LocalDateTime addedAt;
}