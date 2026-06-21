package com.example.Trip_suggestion_web.controller;

import com.example.Trip_suggestion_web.external.TourApiService;
import com.example.Trip_suggestion_web.external.FestivalApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.Trip_suggestion_web.external.weather.WeatherApiService;
import com.example.Trip_suggestion_web.external.weather.WeatherDto;
import com.example.Trip_suggestion_web.entity.RecommendationHistory;
import com.example.Trip_suggestion_web.repository.RecommendationHistoryRepository;
import com.example.Trip_suggestion_web.external.naver.NewsDto;
import com.example.Trip_suggestion_web.external.naver.NaverApiService;
import com.example.Trip_suggestion_web.service.RecommendationEngine;
import jakarta.servlet.http.HttpSession;
import com.example.Trip_suggestion_web.entity.User;
import java.util.List;
import java.util.ArrayList;
import com.example.Trip_suggestion_web.dto.CalendarDay;
import com.example.Trip_suggestion_web.dto.TourDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Controller
public class RecommendationController {

    private final TourApiService tourApiService;
    private final FestivalApiService festivalApiService;
    private final WeatherApiService weatherApiService;
    private final RecommendationHistoryRepository historyRepository;
    private final NaverApiService naverApiService;
    private final RecommendationEngine recommendationEngine;

    public RecommendationController(
            TourApiService tourApiService,
            FestivalApiService festivalApiService,
            WeatherApiService weatherApiService,
            RecommendationHistoryRepository historyRepository,
            NaverApiService naverApiService,
            RecommendationEngine recommendationEngine
    ) {

        this.tourApiService = tourApiService;
        this.festivalApiService = festivalApiService;
        this.weatherApiService = weatherApiService;
        this.historyRepository = historyRepository;
        this.naverApiService = naverApiService;
        this.recommendationEngine = recommendationEngine;
    }

    @PostMapping("/recommend")
    public String recommend(

            @RequestParam String companion,

            @RequestParam String budget,

            @RequestParam String season,

            @RequestParam(required = false)
            List<String> style,

            HttpSession session,

            Model model

    ) {

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        if(loginUser == null){

            model.addAttribute(
                    "loginRequired",
                    true
            );

            return "index";
        }

        // ===== 226개 지역 점수 기반 추천 엔진 호출 (기존 if-else 로직 대체) =====
        RecommendationEngine.RecommendationResult result =
                recommendationEngine.recommend(companion, budget, season, style);

        String destination = result.destination();
        int areaCode = result.areaCode();
        String reason = result.reason();
        // ====================================================================

        List<TourDto> tours =
                tourApiService.getTourList(areaCode);

        List<NewsDto> news =
                naverApiService.getNews(
                        destination + " 여행 후기"
                );

        model.addAttribute(
                "destination",
                destination
        );

        session.setAttribute(
                "destination",
                destination
        );

        session.setAttribute(
                "destinationProvince",
                result.province()
        );

        model.addAttribute(
                "companion",
                companion
        );

        model.addAttribute(
                "budget",
                budget
        );

        model.addAttribute(
                "style",
                style
        );

        model.addAttribute(
                "tours",
                tours
        );

        model.addAttribute(
                "reason",
                reason
        );

        List<CalendarDay> calendarDays =
                new ArrayList<>();

        for (int i = 1; i <= 30; i++) {

            String icon;
            String description;

            if (i <= 7) {

                icon = "☀️";
                description = "실제 예보";

            }

            else {

                icon = "🔍";
                description = "예상 날씨";

            }

            calendarDays.add(
                    new CalendarDay(
                            i + "일",
                            icon,
                            description
                    )
            );

        }

        model.addAttribute(
                "calendarDays",
                calendarDays
        );

        model.addAttribute(
                "news",
                news
        );

        model.addAttribute(
                "season",
                season
        );

        RecommendationHistory history =
                new RecommendationHistory(
                        companion,
                        budget,
                        style != null ? style.toString() : "[]",
                        destination,
                        "AI 추천",
                        loginUser
                );
        historyRepository.save(
                history
        );

        return "recommend";
    }
    @GetMapping("/history")
    public String history(

            @RequestParam(defaultValue = "0")
            int page,

            HttpSession session,

            Model model
    ) {

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        if (loginUser == null) {

            return "redirect:/login";

        }

        Page<RecommendationHistory> histories =

                historyRepository.findByUser(

                        loginUser,

                        PageRequest.of(
                                page,
                                10
                        )
                );

        model.addAttribute(
                "histories",
                histories
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                histories.getTotalPages()
        );

        return "history";

    }

    @PostMapping("/history/delete")
    public String deleteHistory(
            @RequestParam Long id){

        historyRepository.deleteById(id);

        return "redirect:/history";
    }

    @PostMapping("/history/deleteAll")
    public String deleteAllHistory(
            HttpSession session){

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        historyRepository.deleteByUser(
                loginUser
        );

        return "redirect:/history";
    }

    @PostMapping("/history/deletePage")
    public String deletePage(

            @RequestParam int page,

            HttpSession session
    ) {

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        Page<RecommendationHistory> histories =

                historyRepository.findByUser(

                        loginUser,

                        PageRequest.of(
                                page,
                                10
                        )
                );

        historyRepository.deleteAll(
                histories.getContent()
        );

        return "redirect:/history?page=" + page;
    }
}