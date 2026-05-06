package com.example.recommendation.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductType(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0 FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ?
            """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
    }

    public double sumDepositsByType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ? AND t.transaction_type = 'DEPOSIT'
            """;
        return jdbcTemplate.queryForObject(sql, Double.class, userId, productType);
    }

    public double sumWithdrawalsByType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ? AND t.transaction_type = 'WITHDRAWAL'
            """;
        return jdbcTemplate.queryForObject(sql, Double.class, userId, productType);
    }
}