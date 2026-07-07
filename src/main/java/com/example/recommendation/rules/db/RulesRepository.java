package com.example.recommendation.rules.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RulesRepository extends JpaRepository<DynamicRuleEntity, UUID> {
    Optional<DynamicRuleEntity> findByProductId(String productId);
}