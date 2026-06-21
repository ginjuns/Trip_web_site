package com.example.Trip_suggestion_web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.Trip_suggestion_web.external.weather.ClimateApiService;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.Trip_suggestion_web.external.weather.WeatherApiService;
import com.example.Trip_suggestion_web.external.weather.WeatherDto;
import com.example.Trip_suggestion_web.external.weather.WeatherRegionMapper;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import jakarta.servlet.http.HttpSession;

@RestController
public class WeatherController {
    private final ClimateApiService climateApiService;
    private final WeatherApiService weatherApiService;

    public WeatherController(
            ClimateApiService climateApiService,
            WeatherApiService weatherApiService
    ) {

        this.climateApiService = climateApiService;
        this.weatherApiService = weatherApiService;
    }

    // destination(지역명) 기준으로 161개 시군구 단위 정확한 nx,ny를 찾고,
    // ASOS 관측소(stationId)는 destinationProvince(광역자치단체명) 기준 대표값을 사용한다.
    private WeatherRegionMapper.StationInfo resolveStation(HttpSession session) {

        String regionName =
                (String) session.getAttribute("destination");

        String province =
                (String) session.getAttribute("destinationProvince");

        return WeatherRegionMapper.resolve(regionName, province);
    }

    @GetMapping("/weather/detail")
    public Map<String, String> getWeatherDetail(
            @RequestParam String date,
            HttpSession session
    ) throws Exception {

        Map<String, String> result = new HashMap<>();

        WeatherRegionMapper.StationInfo station =
                resolveStation(session);

        int stationId = station.stationId();
        int nx = station.nx();
        int ny = station.ny();

        LocalDate selectedDate =
                LocalDate.parse(date);

        LocalDate today =
                LocalDate.now();

        long days =
                ChronoUnit.DAYS.between(
                        today,
                        selectedDate
                );

        result.put(
                "date",
                date
        );

        if (days >= 0 && days <= 5) {

            WeatherDto weather =
                    weatherApiService.getWeather(
                            selectedDate.format(
                                    java.time.format.DateTimeFormatter.ofPattern(
                                            "yyyyMMdd"
                                    )
                            ),
                            nx,
                            ny
                    );

            if (!weather.getTemperature().equals("정보 없음")
                    && !weather.getSky().equals("정보 없음")) {

                result.put(
                        "message",
                        "📅 실제 기상청 예보\n\n"
                                + "기온 : "
                                + weather.getTemperature()
                                + "\n\n날씨 : "
                                + weather.getSky()
                );

                return result;
            }

        }

        StringBuilder message =
                new StringBuilder();

        double totalTemp = 0;
        double totalRain = 0;
        int rainyCount = 0;
        double totalSun = 0;
        int count = 0;

        message.append(
                "<table class='table table-bordered table-sm text-center'>"

                        + "<thead class='table-light'>"

                        + "<tr>"

                        + "<th>연도</th>"
                        + "<th>날씨</th>"
                        + "<th>평균</th>"
                        + "<th>최고</th>"
                        + "<th>최저</th>"
                        + "<th>강수</th>"

                        + "</tr>"

                        + "</thead>"

                        + "<tbody>"
        );

        for (int year = 2021;
             year <= 2025;
             year++) {

            String monthDay =
                    selectedDate
                            .format(
                                    java.time.format.DateTimeFormatter.ofPattern("MMdd")
                            );

            String targetDate =
                    year + monthDay;

            String response =
                    climateApiService.getClimateData(
                            targetDate,
                            targetDate,
                            stationId
                    );

            ObjectMapper mapper =
                    new ObjectMapper();

            JsonNode root =
                    mapper.readTree(response);

            JsonNode item =
                    root.path("response")
                            .path("body")
                            .path("items")
                            .path("item");

            if (item.isArray() &&
                    item.size() > 0) {

                String avgTemp =
                        item.get(0)
                                .path("avgTa")
                                .asText();

                String maxTemp =
                        item.get(0)
                                .path("maxTa")
                                .asText();

                String minTemp =
                        item.get(0)
                                .path("minTa")
                                .asText();

                String sunHour =
                        item.get(0)
                                .path("sumSsHr")
                                .asText();

                String rain =
                        item.get(0)
                                .path("sumRn")
                                .asText();

                String weatherIcon;
                String weatherText;

                String rainDisplay =
                        (rain == null
                                || rain.isBlank()
                                || rain.equals("null"))
                                ? "0.0"
                                : rain;

                double temp =
                        Double.parseDouble(avgTemp);

                double rainValue = 0;

                if (rain != null
                        && !rain.isBlank()
                        && !rain.equals("null")) {

                    rainValue =
                            Double.parseDouble(rain);

                }

                double sunValue = 0;

                if (sunHour != null
                        && !sunHour.isBlank()
                        && !sunHour.equals("null")) {

                    sunValue =
                            Double.parseDouble(sunHour);

                }

                totalTemp += temp;
                totalRain += rainValue;
                totalSun += sunValue;
                count++;

                if (rainValue >= 1) {
                    rainyCount++;
                }

                if (rainValue >= 5) {

                    weatherIcon = "🌧️";
                    weatherText = "비";

                }
                else if (sunValue >= 7) {

                    weatherIcon = "☀️";
                    weatherText = "맑음";

                }
                else if (sunValue >= 4) {

                    weatherIcon = "⛅";
                    weatherText = "구름 조금";

                }
                else {

                    weatherIcon = "☁️";
                    weatherText = "흐림";

                }

                message.append(

                        "<tr>"

                                + "<td>"
                                + year
                                + "</td>"

                                + "<td>"
                                + weatherIcon
                                + " "
                                + weatherText
                                + "</td>"

                                + "<td>"
                                + avgTemp
                                + "℃</td>"

                                + "<td>"
                                + maxTemp
                                + "℃</td>"

                                + "<td>"
                                + minTemp
                                + "℃</td>"

                                + "<td>"
                                + rainDisplay
                                + "mm</td>"

                                + "</tr>"
                );

            }

        }
        message.append(
                "</tbody></table>"
        );

        double averageTemp =
                totalTemp / count;

        double averageRain =
                totalRain / count;

        double averageSun =
                totalSun / count;

        double rainRate =
                (double) rainyCount / count * 100;

        message.append(
                "<hr>"
                        + "<h5>📊 최근 5년 분석</h5>"

                        + "<div class='alert alert-info'>"

                        + "평균기온 : "
                        + String.format("%.2f", averageTemp)
                        + "℃<br>"

                        + "평균강수량 : "
                        + String.format("%.2f", averageRain)
                        + " mm<br>"

                        + "비가 온 비율 : "
                        + String.format("%.0f", rainRate)
                        + "%<br>"

                        + "평균일조시간 : "
                        + String.format("%.2f", averageSun)
                        + " 시간"

                        + "</div>"
        );

        message.append(
                "\n\n예상 날씨\n"
        );

        if (rainRate >= 60) {

            message.append(
                    "<div class='alert alert-primary'>"
                            + "🌧️ 최근 5년 중 "
                            + rainyCount
                            + "년 동안 비가 왔습니다.<br>"
                            + "비가 올 가능성이 있습니다."
                            + "</div>"
            );

        }
        else if (averageSun >= 7) {

            message.append(
                    "<div class='alert alert-warning'>"
                            + "☀️ 최근 5년 동안 일조시간이 길었습니다.<br>"
                            + "맑고 여행하기 좋은 날씨가 예상됩니다."
                            + "</div>"
            );

        }
        else if (averageTemp >= 20) {

            message.append(
                    "<div class='alert alert-success'>"
                            + "⛅ 비교적 온화한 날씨가 예상됩니다."
                            + "</div>"
            );

        }
        else {

            message.append(
                    "<div class='alert alert-secondary'>"
                            + "☁️ 흐린 날씨가 예상됩니다."
                            + "</div>"
            );

        }

        result.put(
                "message",
                message.toString()
        );

        return result;

    }

    @GetMapping("/weather/calendar")
    public Map<String,String> getCalendarWeather(
            @RequestParam String date,
            HttpSession session
    ) throws Exception {

        Map<String,String> result =
                new HashMap<>();

        WeatherRegionMapper.StationInfo station =
                resolveStation(session);

        int nx = station.nx();
        int ny = station.ny();

        LocalDate selectedDate =
                LocalDate.parse(date);

        LocalDate today =
                LocalDate.now();

        long days =
                ChronoUnit.DAYS.between(
                        today,
                        selectedDate
                );

        String icon;

        if(days >= 0 && days <= 5){

            WeatherDto weather =
                    weatherApiService.getWeather(
                            selectedDate.format(
                                    java.time.format.DateTimeFormatter.ofPattern(
                                            "yyyyMMdd"
                                    )
                            ),
                            nx,
                            ny
                    );

            if(weather.getSky().equals("정보 없음")){

                icon = "📊";

            }
            else if(weather.getSky().contains("비")){

                icon = "🌧️";

            }
            else if(weather.getSky().contains("눈")){

                icon = "❄️";

            }
            else if(weather.getSky().contains("구름")){

                icon = "⛅";

            }
            else if(weather.getSky().contains("흐림")){

                icon = "☁️";

            }
            else{

                icon = "☀️";

            }

        }
        else if(days < 0){

            icon = "";

        }
        else{

            icon = "📊";

        }

        result.put(
                "icon",
                icon
        );

        return result;
    }

}