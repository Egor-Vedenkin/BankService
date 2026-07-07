package com.example.recommendation.rules.db;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rules")
public class DynamicRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String productName;

    @Column(unique = true)
    private String productId;

    private String productText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rule_queries", joinColumns = @JoinColumn(name = "rule_id"))
    @OrderColumn(name = "query_order")
    private List<QueryDto> rule;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }
    public List<QueryDto> getRule() { return rule; }
    public void setRule(List<QueryDto> rule) { this.rule = rule; }
}