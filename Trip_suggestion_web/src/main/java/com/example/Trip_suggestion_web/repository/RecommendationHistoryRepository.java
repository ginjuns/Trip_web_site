package com.example.Trip_suggestion_web.repository;

import com.example.Trip_suggestion_web.entity.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Trip_suggestion_web.entity.User;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecommendationHistoryRepository
        extends JpaRepository<RecommendationHistory, Long> {

    List<RecommendationHistory> findByUser(User user);

    @Transactional
    void deleteByUser(User user);

    Page<RecommendationHistory> findByUser(
            User user,
            Pageable pageable
    );
}