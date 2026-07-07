package com.example.recommendation.controller;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.rule.RecommendationRuleSet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// DTO для ответа API (уже оформлен отдельным классом)
class RecommendationResponseDto {
    private UUID user_id;
    private List<RecommendationDto> recommendations;

    public void setUser_id(UUID userId) {
        this.user_id = userId;
    }

    public void setRecommendations(List<RecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }
}


@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final List<RecommendationRuleSet> ruleSets;

    // Конструкторная инъекция вместо field injection
    public RecommendationController(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<RecommendationResponseDto> getRecommendations(@PathVariable("user_id") UUID user_id) {
        List<RecommendationDto> recommendations = ruleSets.stream()
                .map(rule -> rule.check(user_id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        RecommendationResponseDto response = new RecommendationResponseDto();
        response.setUser_id(user_id);
        response.setRecommendations(recommendations);

        return ResponseEntity.ok(response);
    }
}