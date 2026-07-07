package com.example.recommendation.controller;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.rule.*;
import com.example.recommendation.rules.db.DynamicRuleEntity;
import com.example.recommendation.service.DynamicRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final List<RecommendationRuleSet> fixedRuleSets;
    private final DynamicRuleService dynamicRuleService;

    public RecommendationController(List<RecommendationRuleSet> fixedRuleSets, DynamicRuleService dynamicRuleService) {
        this.fixedRuleSets = fixedRuleSets;
        this.dynamicRuleService = dynamicRuleService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(@PathVariable UUID user_id) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        // 1. Проверяем старые фиксированные правила
        recommendations.addAll(fixedRuleSets.stream()
                .map(rule -> rule.check(user_id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));

        // 2. Проверяем новые динамические правила
        dynamicRuleService.checkUserAgainstRules(user_id).ifPresent(rule -> {
            recommendations.add(new RecommendationDto(
                    rule.getProductId(),
                    rule.getProductName(),
                    rule.getProductText()
            ));
        });

        return ResponseEntity.ok(recommendations);
    }

    /* ========== API для управления динамическими правилами ========== */

    @PostMapping("/rule")
    public ResponseEntity<DynamicRuleEntity> addRule(@RequestBody DynamicRuleEntity rule) {
        // Простая проверка уникальности ID продукта
        if (dynamicRuleService.getAllActiveRules().stream().anyMatch(r -> r.getProductId().equals(rule.getProductId()))) {
            return ResponseEntity.badRequest().build();
        }
        DynamicRuleEntity saved = dynamicRuleService.getAllActiveRules().contains(rule) ? rule : null;
        // На практике здесь должен быть вызов сервиса сохранения в RulesRepository
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/rule")
    public ResponseEntity<List<DynamicRuleEntity>> listRules() {
        return ResponseEntity.ok(dynamicRuleService.getAllActiveRules());
    }

    @DeleteMapping("/rule/{product_id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String product_id) {
        // На практике здесь должен быть вызов service.delete(product_id)
        return ResponseEntity.noContent().build();
    }
}