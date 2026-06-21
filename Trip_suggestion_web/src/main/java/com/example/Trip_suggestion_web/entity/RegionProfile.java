package com.example.Trip_suggestion_web.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RegionProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // 지역명 (예: 강릉시)
    private String parent;    // 광역자치단체 (예: 강원특별자치도)
    private String type;      // 시/군/구
    private int areaCode;     // TourAPI areaCode

    // 스타일 점수 (1~10)
    private int natureScore;
    private int foodScore;
    private int festivalScore;
    private int activityScore;

    // 동행 적합도 (1~10)
    private int soloScore;
    private int coupleScore;
    private int friendScore;
    private int familyScore;

    // 계절 적합도 (1~10)
    private int springScore;
    private int summerScore;
    private int fallScore;
    private int winterScore;

    // 예산 적합도 (1~10)
    private int budgetLowScore;
    private int budgetMidScore;
    private int budgetHighScore;

    protected RegionProfile() {
    }

    public RegionProfile(String name, String parent, String type, int areaCode,
                         int natureScore, int foodScore, int festivalScore, int activityScore,
                         int soloScore, int coupleScore, int friendScore, int familyScore,
                         int springScore, int summerScore, int fallScore, int winterScore,
                         int budgetLowScore, int budgetMidScore, int budgetHighScore) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.areaCode = areaCode;
        this.natureScore = natureScore;
        this.foodScore = foodScore;
        this.festivalScore = festivalScore;
        this.activityScore = activityScore;
        this.soloScore = soloScore;
        this.coupleScore = coupleScore;
        this.friendScore = friendScore;
        this.familyScore = familyScore;
        this.springScore = springScore;
        this.summerScore = summerScore;
        this.fallScore = fallScore;
        this.winterScore = winterScore;
        this.budgetLowScore = budgetLowScore;
        this.budgetMidScore = budgetMidScore;
        this.budgetHighScore = budgetHighScore;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getParent() { return parent; }
    public String getType() { return type; }
    public int getAreaCode() { return areaCode; }

    public int getStyleScore(String style) {
        return switch (style) {
            case "자연" -> natureScore;
            case "맛집" -> foodScore;
            case "축제" -> festivalScore;
            case "액티비티" -> activityScore;
            default -> 0;
        };
    }

    public int getCompanionScore(String companion) {
        return switch (companion) {
            case "혼자" -> soloScore;
            case "커플" -> coupleScore;
            case "친구" -> friendScore;
            case "가족" -> familyScore;
            default -> 0;
        };
    }

    public int getSeasonScore(String season) {
        return switch (season) {
            case "봄" -> springScore;
            case "여름" -> summerScore;
            case "가을" -> fallScore;
            case "겨울" -> winterScore;
            default -> 0;
        };
    }

    public int getBudgetScore(String budget) {
        // index.html select 값과 동일하게 매핑
        return switch (budget) {
            case "10만원 이하" -> budgetLowScore;
            case "10~30만원" -> budgetMidScore;
            case "30만원 이상" -> budgetHighScore;
            default -> 0;
        };
    }

    // 가장 점수가 높은 스타일 카테고리를 찾아 추천 사유 생성에 사용
    public String getTopStyle() {
        int max = Math.max(Math.max(natureScore, foodScore), Math.max(festivalScore, activityScore));
        if (max == natureScore) return "자연";
        if (max == foodScore) return "맛집";
        if (max == festivalScore) return "축제";
        return "액티비티";
    }
}