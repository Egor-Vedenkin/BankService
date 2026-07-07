package com.example.recommendation.dto;

public class RecommendationDto {
    private String id;
    private String name;
    private String text;

    public RecommendationDto(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public String getText() { return text; }
}
