package com.example.Trip_suggestion_web.external.naver;

public class NewsDto {

    private String title;

    private String link;

    public NewsDto(
            String title,
            String link
    ) {

        this.title = title;
        this.link = link;

    }

    public String getTitle() {

        return title;

    }

    public String getLink() {

        return link;

    }

}