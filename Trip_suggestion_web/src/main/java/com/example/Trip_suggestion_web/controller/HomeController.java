package com.example.Trip_suggestion_web.controller;

import com.example.Trip_suggestion_web.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.Trip_suggestion_web.external.FestivalApiService;
import com.example.Trip_suggestion_web.dto.TourDto;
import com.example.Trip_suggestion_web.entity.RegionProfile;
import com.example.Trip_suggestion_web.repository.RegionProfileRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Controller
public class HomeController {
    private final FestivalApiService festivalApiService;
    private final RegionProfileRepository regionProfileRepository;

    public HomeController(
            FestivalApiService festivalApiService,
            RegionProfileRepository regionProfileRepository
    ) {

        this.festivalApiService =
                festivalApiService;
        this.regionProfileRepository =
                regionProfileRepository;
    }

    @GetMapping("/")
    public String home(

            HttpSession session,

            Model model

    ) {

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        model.addAttribute(
                "loginUser",
                loginUser
        );

        List<TourDto> festivals =
                festivalApiService.getMainFestivalList();

        model.addAttribute(
                "festivals",
                festivals
        );

        model.addAttribute(
                "allFestivals",
                festivals
        );

        model.addAttribute(
                "currentMonth",
                LocalDate.now().getMonthValue()
        );

        // 검색창 자동완성용 - DB에 적재된 226개(+제주시/서귀포시/세종) 지역명 전체
        List<String> allRegionNames =
                regionProfileRepository.findAllByOrderByNameAsc()
                        .stream()
                        .map(RegionProfile::getName)
                        .collect(Collectors.toList());

        model.addAttribute(
                "allRegionNames",
                allRegionNames
        );

        return "index";

    }

}