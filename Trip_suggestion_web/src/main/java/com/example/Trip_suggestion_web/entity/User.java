package com.example.Trip_suggestion_web.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Long id;

    private String username;

    private String password;

    private String name;

    public User() {

    }

    public User(

            String username,

            String password,

            String name

    ) {

        this.username =
                username;

        this.password =
                password;

        this.name =
                name;

    }

    public Long getId() {

        return id;

    }

    public String getUsername() {

        return username;

    }

    public String getPassword() {

        return password;

    }

    public String getName() {

        return name;

    }

}