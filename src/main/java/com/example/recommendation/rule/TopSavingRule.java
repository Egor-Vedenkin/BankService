package com.example.recommendation.rule;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {
    @Autowired
    private RecommendationRepository repository;

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        double sumDebitDeposit = repository.sumDepositsByType(userId, "DEBIT");
        double sumSavingDeposit = repository.sumDepositsByType(userId, "SAVING");
        double sumDebitWithdrawal = repository.sumWithdrawalsByType(userId, "DEBIT");

        boolean cond1 = hasDebit &&
                (sumDebitDeposit >= 50000 || sumSavingDeposit >= 50000);
        boolean cond2 = sumDebitDeposit > sumDebitWithdrawal;

        if (cond1 && cond2) {
            return Optional.of(new RecommendationDto(
                    "59efc529-2fff-41af-baff-90ccd7402925",
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком! ..."
            ));
        }
        return Optional.empty();
    }
}