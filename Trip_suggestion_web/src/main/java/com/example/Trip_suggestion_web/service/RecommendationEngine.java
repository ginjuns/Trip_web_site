package com.example.Trip_suggestion_web.service;

import com.example.Trip_suggestion_web.entity.RegionProfile;
import com.example.Trip_suggestion_web.repository.RegionProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class RecommendationEngine {

    // 가중치: 필요하면 여기 숫자만 조정해서 추천 성향을 튜닝할 수 있다.
    private static final double W_COMPANION = 1.0;
    private static final double W_SEASON = 1.0;
    private static final double W_STYLE = 1.5;   // 다중 선택이라 비중을 조금 더 둠
    private static final double W_BUDGET = 0.7;

    private final RegionProfileRepository regionProfileRepository;
    private final Random random = new Random();

    public RecommendationEngine(RegionProfileRepository regionProfileRepository) {
        this.regionProfileRepository = regionProfileRepository;
    }

    public RecommendationResult recommend(String companion, String budget,
                                          String season, List<String> styles) {

        List<RegionProfile> all = regionProfileRepository.findAll();

        if (all.isEmpty()) {
            throw new IllegalStateException("지역 데이터가 비어있습니다. RegionDataLoader 실행 여부를 확인하세요.");
        }

        List<ScoredRegion> scored = all.stream()
                .map(r -> new ScoredRegion(r, calculateScore(r, companion, budget, season, styles)))
                .sorted(Comparator.comparingDouble(ScoredRegion::score).reversed())
                .toList();

        // 상위 5개 중 랜덤으로 하나 선택 -> 같은 조건이어도 매번 똑같은 지역만 나오지 않게
        int poolSize = Math.min(5, scored.size());
        ScoredRegion picked = scored.get(random.nextInt(poolSize));

        String reason = buildReason(picked.region(), companion, season, styles);

        return new RecommendationResult(
                picked.region().getName(),
                picked.region().getAreaCode(),
                picked.region().getParent(),
                reason
        );
    }

    private double calculateScore(RegionProfile region, String companion, String budget,
                                  String season, List<String> styles) {

        double styleAvg = 0;
        if (styles != null && !styles.isEmpty()) {
            styleAvg = styles.stream()
                    .mapToInt(region::getStyleScore)
                    .average()
                    .orElse(0);
        }

        double score = 0;
        score += region.getCompanionScore(companion) * W_COMPANION;
        score += region.getSeasonScore(season) * W_SEASON;
        score += styleAvg * W_STYLE;
        score += region.getBudgetScore(budget) * W_BUDGET;

        return score;
    }

    private String buildReason(RegionProfile region, String companion, String season, List<String> styles) {

        String styleText = (styles == null || styles.isEmpty())
                ? region.getTopStyle()
                : String.join(", ", styles);

        return String.format(
                "%s(을)를 좋아하는 %s 여행객에게 %s %s 시즌 추천 지역입니다.",
                styleText, companion, season, region.getType()
        );
    }

    private record ScoredRegion(RegionProfile region, double score) {}

    public record RecommendationResult(String destination, int areaCode, String province, String reason) {}
}