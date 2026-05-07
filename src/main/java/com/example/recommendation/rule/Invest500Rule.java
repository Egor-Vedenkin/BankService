package com.example.recommendation.rule;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500Rule implements RecommendationRuleSet {
    private final RecommendationRepository repository;

    @Autowired
    public Invest500Rule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        boolean hasInvest = repository.hasProductType(userId, "INVEST");
        double sumSaving = repository.sumDepositsByType(userId, "SAVING");

        if (hasDebit && !hasInvest && sumSaving > 1000) {
            return Optional.of(new RecommendationDto(
                    "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Это уникальная возможность начать инвестировать с минимальными рисками и получать налоговые льготы. Подробности — у вашего менеджера."
            ));
        }
        return Optional.empty();
    }
}