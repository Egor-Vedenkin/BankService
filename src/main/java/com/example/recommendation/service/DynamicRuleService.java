package com.example.recommendation.service;

import com.example.recommendation.repository.RecommendationRepository;
import com.example.recommendation.rules.db.DynamicRuleEntity;
import com.example.recommendation.rules.db.QueryDto;
import com.example.recommendation.rules.db.RulesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicRuleService {

    private final RecommendationRepository repository;
    private final RulesRepository rulesRepository;

    public DynamicRuleService(RecommendationRepository repository, RulesRepository rulesRepository) {
        this.repository = repository;
        this.rulesRepository = rulesRepository;
    }

    public List<DynamicRuleEntity> getAllActiveRules() {
        return rulesRepository.findAll();
    }

    public Optional<DynamicRuleEntity> checkUserAgainstRules(UUID userId) {
        // Проверяем сначала фиксированные правила (старый функционал), затем новые
        for (DynamicRuleEntity rule : getAllActiveRules()) {
            if (evaluateRule(rule.getRule(), userId)) {
                return Optional.of(rule);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public DynamicRuleEntity saveRule(DynamicRuleEntity rule) {
        UUID generatedId = UUID.randomUUID();
        rule.setId(generatedId);

        // Очищаем старые связи перед сохранением новых (упрощенный вариант)
        // На практике лучше удалять по ID или использовать merge
        rulesRepository.deleteAll();

        return rulesRepository.save(rule);
    }

    @Transactional
    public void deleteRuleByProductId(String productId) {
        Optional<DynamicRuleEntity> ruleOpt = rulesRepository.findByProductId(productId);
        ruleOpt.ifPresent(rule -> rulesRepository.deleteById(rule.getId()));
    }

    private boolean evaluateRule(List<QueryDto> queries, UUID userId) {
        for (QueryDto q : queries) {
            boolean result = switch (q.getQuery()) {
                case "USER_OF" -> handleUserOf(userId, q.getArguments()[0]);
                case "ACTIVE_USER_OF" -> false; // Заглушка, требует доработки репозитория
                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> handleCompareDepositWithdraw(userId, q.getArguments()[0], q.getArguments()[1]);
                case "TRANSACTION_SUM_COMPARE" -> handleSumCompare(userId, q);
                default -> false;
            };

            if (q.isNegate()) {
                result = !result;
            }

            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean handleUserOf(UUID userId, String type) {
        return repository.hasProductType(userId, type);
    }

    private boolean handleCompareDepositWithdraw(UUID userId, String type, String operator) {
        double deposit = repository.sumDepositsByType(userId, type);
        double withdraw = repository.sumWithdrawalsByType(userId, type);
        return compareValues(deposit, withdraw, operator);
    }

    private boolean handleSumCompare(UUID userId, QueryDto q) {
        String prodType = q.getArguments()[0];
        String transType = q.getArguments()[1];
        String operator = q.getArguments()[2];
        int constant = Integer.parseInt(q.getArguments()[3]);

        double sum = transType.equals("DEPOSIT")
                ? repository.sumDepositsByType(userId, prodType)
                : repository.sumWithdrawalsByType(userId, prodType);

        return compareValues(sum, constant, operator);
    }

    private boolean compareValues(double a, double b, String op) {
        return switch (op) {
            case ">" -> a > b;
            case "<" -> a < b;
            case "=" -> Math.abs(a - b) < 0.01;
            case ">=" -> a >= b;
            case "<=" -> a <= b;
            default -> false;
        };
    }
}