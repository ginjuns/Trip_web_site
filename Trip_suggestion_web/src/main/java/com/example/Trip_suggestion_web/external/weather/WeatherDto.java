package com.example.Trip_suggestion_web.external.weather;

public class WeatherDto {

    private String temperature;

    private String sky;

    public WeatherDto(
            String temperature,
            String sky
    ) {

        this.temperature = temperature;
        this.sky = sky;

    }

    public String getTemperature() {

        return temperature;

    }

    public String getSky() {

        return sky;

    }

}