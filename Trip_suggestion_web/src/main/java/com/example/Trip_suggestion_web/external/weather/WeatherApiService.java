package com.example.Trip_suggestion_web.external.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherApiService {

    @Value("${weather.api.auth-key}")
    private String authKey;

    public WeatherDto getWeather(String targetDate,int nx, int ny) {

        try {
            String today =
                    LocalDate.now()
                            .format(
                                    DateTimeFormatter.ofPattern("yyyyMMdd")
                            );

            LocalTime now = LocalTime.now();
            String baseTime = "2300";

            if (now.isAfter(LocalTime.of(23, 0))) {
                baseTime = "2300";
            }
            else if (now.isAfter(LocalTime.of(20, 0))) {
                baseTime = "2000";
            }
            else if (now.isAfter(LocalTime.of(17, 0))) {
                baseTime = "1700";
            }
            else if (now.isAfter(LocalTime.of(14, 0))) {
                baseTime = "1400";
            }
            else if (now.isAfter(LocalTime.of(11, 0))) {
                baseTime = "1100";
            }
            else if (now.isAfter(LocalTime.of(8, 0))) {
                baseTime = "0800";
            }
            else if (now.isAfter(LocalTime.of(5, 0))) {
                baseTime = "0500";
            }
            else {
                baseTime = "0200";
            }

            String url =
                    "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst"
                            + "?pageNo=1"
                            + "&numOfRows=1000"
                            + "&dataType=XML"
                            + "&base_date=" + today
                            + "&base_time=" + baseTime
                            + "&nx=" + nx
                            + "&ny=" + ny
                            + "&authKey=" + authKey;

            RestTemplate restTemplate =
                    new RestTemplate();

            String response =
                    restTemplate.getForObject(
                            url,
                            String.class
                    );

            String temperature = "정보 없음";
            String sky = "정보 없음";
            String pty = "0";

            Document document =
                    DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(
                                    new ByteArrayInputStream(
                                            response.getBytes()
                                    )
                            );

            NodeList categories =
                    document.getElementsByTagName(
                            "category"
                    );

            NodeList values =
                    document.getElementsByTagName(
                            "fcstValue"
                    );

            NodeList dates =
                    document.getElementsByTagName(
                            "fcstDate"
                    );

            for (int i = 0; i < categories.getLength(); i++) {

                String category =
                        categories.item(i)
                                .getTextContent();

                String value =
                        values.item(i)
                                .getTextContent();

                String fcstDate =
                        dates.item(i)
                                .getTextContent();

                if (!fcstDate.equals(targetDate)) {

                    continue;

                }

                if (category.equals("TMP")) {

                    temperature =
                            value + "℃";

                }

                if (category.equals("SKY")) {

                    if (value.equals("1")) {

                        sky = "맑음 ☀️";

                    }

                    else if (value.equals("3")) {

                        sky = "구름 많음 ⛅";

                    }

                    else if (value.equals("4")) {

                        sky = "흐림 ☁️";

                    }

                }

                if (category.equals("PTY")) {

                    pty = value;

                }

                if (!temperature.equals("정보 없음")
                        && !sky.equals("정보 없음")) {

                    break;

                }

            }

            if (pty.equals("1")) {

                sky = "비 🌧️";

            }
            else if (pty.equals("2")) {

                sky = "비/눈 🌨️";

            }
            else if (pty.equals("3")) {

                sky = "눈 ❄️";

            }
            else if (pty.equals("4")) {

                sky = "소나기 🌦️";

            }

            return new WeatherDto(
                    temperature,
                    sky
            );

        }

        catch (Exception e) {

            e.printStackTrace();

            return new WeatherDto(
                    "오류",
                    "오류"
            );

        }

    }

}