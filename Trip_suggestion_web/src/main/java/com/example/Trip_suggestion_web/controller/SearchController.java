package com.example.Trip_suggestion_web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.Trip_suggestion_web.external.TourApiService;
import com.example.Trip_suggestion_web.external.naver.NaverApiService;
import com.example.Trip_suggestion_web.external.naver.NewsDto;
import com.example.Trip_suggestion_web.dto.TourDto;
import com.example.Trip_suggestion_web.dto.CalendarDay;
import com.example.Trip_suggestion_web.entity.RegionProfile;
import com.example.Trip_suggestion_web.repository.RegionProfileRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.Trip_suggestion_web.entity.RecommendationHistory;
import com.example.Trip_suggestion_web.entity.User;
import com.example.Trip_suggestion_web.repository.RecommendationHistoryRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class SearchController {

    private final TourApiService tourApiService;
    private final NaverApiService naverApiService;
    private final RecommendationHistoryRepository historyRepository;
    private final RegionProfileRepository regionProfileRepository;

    public SearchController(
            TourApiService tourApiService,
            NaverApiService naverApiService,
            RecommendationHistoryRepository historyRepository,
            RegionProfileRepository regionProfileRepository
    ) {

        this.tourApiService = tourApiService;
        this.naverApiService = naverApiService;
        this.historyRepository = historyRepository;
        this.regionProfileRepository = regionProfileRepository;
    }

    @GetMapping("/search")
    public String search(
            @RequestParam String keyword,
            HttpSession session,
            Model model) {

        // 226개(+제주시/서귀포시/세종) 지역 DB에서 정확히 일치하는 지역을 찾는다.
        Optional<RegionProfile> regionOpt =
                regionProfileRepository.findByName(keyword);

        if (regionOpt.isEmpty()) {

            model.addAttribute(
                    "error",
                    "존재하지 않는 지역입니다."
            );

            return "search_result";
        }

        RegionProfile region = regionOpt.get();
        int areaCode = region.getAreaCode();

        List<TourDto> tours =
                tourApiService.getTourList(areaCode);

        List<NewsDto> news =
                naverApiService.getNews(
                        keyword + " 여행"
                );

        // 날씨 위젯(WeatherController)이 광역자치단체 기준으로 관측소를 찾을 수 있도록 세션에 저장
        session.setAttribute("destination", keyword);
        session.setAttribute("destinationProvince", region.getParent());

        // recommend.html과 동일한 30일 캘린더 위젯을 검색 결과에도 띄우기 위한 데이터
        List<CalendarDay> calendarDays = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            String icon = (i <= 7) ? "☀️" : "🔍";
            String description = (i <= 7) ? "실제 예보" : "예상 날씨";
            calendarDays.add(new CalendarDay(i + "일", icon, description));
        }
        model.addAttribute("calendarDays", calendarDays);

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        if (loginUser != null) {

            RecommendationHistory history =
                    new RecommendationHistory(
                            "-",
                            "-",
                            "-",
                            keyword,
                            "지역 검색",
                            loginUser
                    );

            historyRepository.save(history);
        }

        model.addAttribute(
                "region",
                keyword
        );

        model.addAttribute(
                "tours",
                tours
        );

        model.addAttribute(
                "news",
                news
        );

        return "search_result";
    }
}