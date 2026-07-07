package com.example.recommendation.rule;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {
    private final RecommendationRepository repository;

    @Autowired
    public TopSavingRule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        double sumDebitDeposit = repository.sumDepositsByType(userId, "DEBIT");
        double sumSavingDeposit = repository.sumDepositsByType(userId, "SAVING");
        double sumDebitWithdrawal = repository.sumWithdrawalsByType(userId, "DEBIT");

        boolean cond1 = hasDebit && (sumDebitDeposit >= 50_000 || sumSavingDeposit >= 50_000);
        boolean cond2 = sumDebitDeposit > sumDebitWithdrawal;

        if (cond1 && cond2) {
            return Optional.of(new RecommendationDto(
                    "59efc529-2fff-41af-baff-90ccd7402925",
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком! Это удобный инструмент для накопления средств с повышенными процентами и гибкими условиями пополнения. Начните копить на мечту уже сегодня!"
            ));
        }
        return Optional.empty();
    }
}