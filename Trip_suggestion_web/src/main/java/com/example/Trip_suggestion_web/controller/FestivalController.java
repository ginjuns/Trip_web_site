package com.example.Trip_suggestion_web.controller;

import com.example.Trip_suggestion_web.external.FestivalApiService;
import com.example.Trip_suggestion_web.dto.TourDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FestivalController {

    private final FestivalApiService festivalApiService;

    public FestivalController(
            FestivalApiService festivalApiService
    ) {

        this.festivalApiService =
                festivalApiService;

    }

    @GetMapping("/festivals")
    public String festivals(
            Model model
    ) {

        List<TourDto> festivals =
                festivalApiService.getAllFestivalList();

        model.addAttribute(
                "festivals",
                festivals
        );

        return "festivals";

    }

}