package com.example.Trip_suggestion_web.entity;

import jakarta.persistence.*;
import com.example.Trip_suggestion_web.entity.User;

@Entity
public class RecommendationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companion;
    private String budget;
    private String style;
    private String destination;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String type;

    public RecommendationHistory() {
    }

    public RecommendationHistory(
            String companion,
            String budget,
            String style,
            String destination,
            String type,
            User user
    ) {

        this.companion = companion;
        this.budget = budget;
        this.style = style;
        this.destination = destination;
        this.type = type;
        this.user = user;
    }

    public Long getId() { return id; }

    public String getCompanion() {
        return companion;
    }

    public String getBudget() {
        return budget;
    }

    public String getStyle() {
        return style;
    }

    public String getDestination() {
        return destination;
    }

    public String getType() {return type;}

    public User getUser() {return user;}
}