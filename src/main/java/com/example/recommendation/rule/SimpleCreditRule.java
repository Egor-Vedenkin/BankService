package com.example.recommendation.rule;

import com.example.recommendation.dto.RecommendationDto;
import com.example.recommendation.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRule implements RecommendationRuleSet {
    private final RecommendationRepository repository;

    @Autowired
    public SimpleCreditRule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasCredit = repository.hasProductType(userId, "CREDIT");
        double sumDebitDeposit = repository.sumDepositsByType(userId, "DEBIT");
        double sumDebitWithdrawal = repository.sumWithdrawalsByType(userId, "DEBIT");

        if (!hasCredit && sumDebitDeposit > sumDebitWithdrawal && sumDebitWithdrawal > 100_000) {
            return Optional.of(new RecommendationDto(
                    "ab138afb-f3ba-4a93-b74f-0fcee86d447f",
                    "Простой кредит",
                    "Ищете способ быстро и без лишних хлопот получить нужную сумму? Наш банк предлагает простой кредит с минимальной ставкой и быстрым одобрением. Подайте заявку онлайн уже сегодня!"
            ));
        }
        return Optional.empty();
    }
}