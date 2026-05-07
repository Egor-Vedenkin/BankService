package com.example.recommendation.controller;

import com.example.recommendation.dto.*;
import com.example.recommendation.rule.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.*;


// DTO для ответа API (отдельный класс)
class RecommendationResponseDto {
    private UUID user_id;
    private List<RecommendationDto> recommendations;

    public void setUser_id(UUID userId) {
        this.user_id = userId;
    }

    public void setRecommendations(List<RecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }

    // Конструктор, геттеры и сеттеры опущены для краткости (можно автогенерировать)
}


@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final List<RecommendationRuleSet> ruleSets;

    @Autowired
    public RecommendationController(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
        // Конструкторная инъекция — зависимости явные
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<RecommendationResponseDto> getRecommendations(@PathVariable UUID user_id) {
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