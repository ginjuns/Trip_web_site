package com.example.Trip_suggestion_web.dto;

public class CalendarDay {

    private String date;

    private String icon;

    private String description;

    public CalendarDay(
            String date,
            String icon,
            String description
    ) {

        this.date = date;

        this.icon = icon;

        this.description = description;

    }

    public String getDate() {

        return date;

    }

    public String getIcon() {

        return icon;

    }

    public String getDescription() {

        return description;

    }

}