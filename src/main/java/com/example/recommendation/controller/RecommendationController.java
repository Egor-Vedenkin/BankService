package com.example.recommendation.controller;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.rule.RecommendationRuleSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    @Autowired
    private List<RecommendationRuleSet> ruleSets;

    @GetMapping("/{user_id}")
    public ResponseEntity<Map<String, Object>> getRecommendations(@PathVariable UUID user_id) {
        List<RecommendationDto> recommendations = ruleSets.stream()
                .map(rule -> rule.check(user_id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", user_id);
        response.put("recommendations", recommendations);

        return ResponseEntity.ok(response);
    }
}