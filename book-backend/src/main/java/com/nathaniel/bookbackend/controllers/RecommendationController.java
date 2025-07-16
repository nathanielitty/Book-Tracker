package com.nathaniel.bookbackend.controllers;

import com.nathaniel.bookbackend.dto.RecommendationDto;
import com.nathaniel.bookbackend.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recService;

    @GetMapping
    public ResponseEntity<List<RecommendationDto>> getRecommendations(Principal user) {
        return ResponseEntity.ok(recService.getRecommendations(user.getName()));
    }
}