package com.example.Trip_suggestion_web.repository;

import com.example.Trip_suggestion_web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);

}