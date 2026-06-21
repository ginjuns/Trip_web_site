package com.example.Trip_suggestion_web.repository;

import com.example.Trip_suggestion_web.entity.RegionProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionProfileRepository extends JpaRepository<RegionProfile, Long> {

    List<RegionProfile> findByNameContaining(String keyword);

    boolean existsByName(String name);

    // 검색 기능(SearchController)에서 정확히 일치하는 지역을 찾을 때 사용
    Optional<RegionProfile> findByName(String name);

    // 자동완성 목록(index.html)을 가나다순으로 내려줄 때 사용
    List<RegionProfile> findAllByOrderByNameAsc();
}