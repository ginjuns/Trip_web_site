package com.example.Trip_suggestion_web.external;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.Trip_suggestion_web.dto.TourDto;
import java.util.ArrayList;
import java.util.List;


@Service
public class TourApiService {

    @Value("${tour.api.key}")
    private String serviceKey;

    public List<TourDto> getTourList(int areaCode) {

        List<TourDto> tourList =
                new ArrayList<>();
        try {

            String url =
                    "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                            + "?serviceKey=" + serviceKey
                            + "&MobileOS=ETC"
                            + "&MobileApp=TripMate"
                            + "&_type=json"
                            + "&areaCode=" + areaCode
                            + "&contentTypeId=12"
                            + "&numOfRows=6"
                            + "&pageNo=1";

            RestTemplate restTemplate = new RestTemplate();

            String response =
                    restTemplate.getForObject(url, String.class);

            JSONObject jsonObject =
                    new JSONObject(response);

            JSONArray items =
                    jsonObject
                            .getJSONObject("response")
                            .getJSONObject("body")
                            .getJSONObject("items")
                            .getJSONArray("item");

            for(int i=0;i<items.length();i++){

                JSONObject item =
                        items.getJSONObject(i);

                String title =
                        item.optString(
                                "title",
                                ""
                        );

                String image =
                        item.optString(
                                "firstimage",
                                "https://placehold.co/600x400"
                        );

                String address =
                        item.optString(
                                "addr1",
                                "주소 정보 없음"
                        );

                tourList.add(

                        new TourDto(
                                title,
                                image,
                                address
                        )

                );

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return tourList;

    }

}