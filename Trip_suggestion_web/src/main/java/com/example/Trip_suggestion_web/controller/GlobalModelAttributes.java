package com.example.Trip_suggestion_web.controller;

import com.example.Trip_suggestion_web.entity.RegionProfile;
import com.example.Trip_suggestion_web.repository.RegionProfileRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 모든 화면(index, recommend, search_result, festivals 등)의 지역 검색/자동완성이
 * 항상 DB에 실제로 존재하는 지역명("서울특별시", "가평군" 등)만 쓰도록,
 * allRegionNames를 모든 Model에 공통으로 내려준다.
 *
 * region_profiles.csv가 바뀌어도(지역 통합/분리 등) 화면 쪽 하드코딩을
 * 따로 고칠 필요가 없어진다.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final RegionProfileRepository regionProfileRepository;

    public GlobalModelAttributes(RegionProfileRepository regionProfileRepository) {
        this.regionProfileRepository = regionProfileRepository;
    }

    @ModelAttribute("allRegionNames")
    public List<String> allRegionNames() {

        return regionProfileRepository.findAllByOrderByNameAsc()
                .stream()
                .map(RegionProfile::getName)
                .collect(Collectors.toList());
    }
}