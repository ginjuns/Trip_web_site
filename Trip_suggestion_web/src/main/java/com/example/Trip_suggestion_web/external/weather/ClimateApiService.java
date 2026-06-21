package com.example.Trip_suggestion_web.external.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClimateApiService {

    @Value("${climate.api.key}")
    private String serviceKey;

    public String getClimateData(
            String startDate,
            String endDate,
            int stationId
    ) {

        String url =
                "https://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList"
                        + "?serviceKey=" + serviceKey
                        + "&pageNo=1"
                        + "&numOfRows=10"
                        + "&dataType=JSON"
                        + "&dataCd=ASOS"
                        + "&dateCd=DAY"
                        + "&startDt=" + startDate
                        + "&endDt=" + endDate
                        + "&stnIds=" + stationId;

        RestTemplate restTemplate =
                new RestTemplate();

        return restTemplate.getForObject(
                url,
                String.class
        );

    }

}