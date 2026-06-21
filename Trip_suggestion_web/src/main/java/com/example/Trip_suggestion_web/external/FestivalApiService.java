package com.example.Trip_suggestion_web.external;

import com.example.Trip_suggestion_web.dto.TourDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class FestivalApiService {

    @Value("${tour.api.key}")
    private String serviceKey;

    public List<TourDto> getFestivalList(int areaCode, String season) {

        List<TourDto> festivalList =
                new ArrayList<>();

        try {

            String eventDate;

            if(season.equals("봄")){

                eventDate = "20260301";

            }
            else if(season.equals("여름")){

                eventDate = "20260601";

            }
            else if(season.equals("가을")){

                eventDate = "20260901";

            }
            else{

                eventDate = "20261201";

            }

            String today =
                    LocalDate.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd"
                                    )
                            );

            String url =
                    "https://apis.data.go.kr/B551011/KorService2/searchFestival2"
                            + "?serviceKey=" + serviceKey
                            + "&MobileOS=ETC"
                            + "&MobileApp=TripMate"
                            + "&_type=json"
                            + "&eventStartDate=" + eventDate
                            + "&numOfRows=1000"
                            + "&pageNo=1";

            RestTemplate restTemplate =
                    new RestTemplate();

            String response =
                    restTemplate.getForObject(
                            url,
                            String.class
                    );
            System.out.println(url);

            System.out.println(response);

            JSONObject jsonObject =
                    new JSONObject(response);

            JSONArray items =
                    jsonObject
                            .getJSONObject("response")
                            .getJSONObject("body")
                            .getJSONObject("items")
                            .getJSONArray("item");

            for (int i = 0; i < items.length(); i++) {

                JSONObject item =
                        items.getJSONObject(i);

                String image =
                        item.optString(
                                "firstimage",
                                ""
                        );

                if (image == null ||
                        image.isBlank()) {

                    continue;

                }

                String title =
                        item.optString(
                                "title",
                                ""
                        );

                String address =
                        item.optString(
                                "addr1",
                                "주소 정보 없음"
                        );

                String startDate =
                        item.optString(
                                "eventstartdate",
                                ""
                        );

                String endDate =
                        item.optString(
                                "eventenddate",
                                ""
                        );

                if(areaCode == 1 &&
                        !address.contains("서울")) {

                    continue;

                }

                if(areaCode == 6 &&
                        !address.contains("부산")) {

                    continue;

                }

                if(areaCode == 32 &&
                        !address.contains("강원")) {

                    continue;

                }

                if(areaCode == 39 &&
                        !address.contains("제주")) {

                    continue;

                }

                festivalList.add(

                        new TourDto(
                                title,
                                image,
                                address,
                                startDate,
                                endDate
                        )

                );

                if (festivalList.size() >= 6) {

                    break;

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return festivalList;

    }

    public List<TourDto> getMainFestivalList() {

        List<TourDto> festivalList =
                new ArrayList<>();

        try {

            String today =
                    LocalDate.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd"
                                    )
                            );

            int currentMonth =
                    LocalDate.now()
                            .getMonthValue();

            String url =
                    "https://apis.data.go.kr/B551011/KorService2/searchFestival2"
                            + "?serviceKey=" + serviceKey
                            + "&MobileOS=ETC"
                            + "&MobileApp=TripMate"
                            + "&_type=json"
                            + "&eventStartDate="
                            + LocalDate.now()
                            .withDayOfMonth(1)
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd"
                                    )
                            )
                            + "&numOfRows=1000"
                            + "&pageNo=1";

            RestTemplate restTemplate =
                    new RestTemplate();

            String response =
                    restTemplate.getForObject(
                            url,
                            String.class
                    );

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

                String image =
                        item.optString(
                                "firstimage",
                                ""
                        );

                if(image.isBlank()){

                    continue;

                }

                String startDate =
                        item.optString(
                                "eventstartdate",
                                ""
                        );

                String endDate =
                        item.optString(
                                "eventenddate",
                                ""
                        );

                if(startDate.isBlank()){

                    continue;

                }

                int festivalMonth =
                        Integer.parseInt(
                                startDate.substring(4,6)
                        );

                if(festivalMonth != currentMonth){

                    continue;

                }

                if(endDate.compareTo(today) < 0){

                    continue;

                }

                String title =
                        item.optString(
                                "title",
                                ""
                        );

                String address =
                        item.optString(
                                "addr1",
                                ""
                        );

                festivalList.add(

                        new TourDto(
                                title,
                                image,
                                address,
                                startDate,
                                endDate
                        )

                );

            }

            festivalList.sort(

                    (a, b) -> {

                        String todayDate =
                                LocalDate.now()
                                        .format(
                                                DateTimeFormatter.ofPattern(
                                                        "yyyyMMdd"
                                                )
                                        );

                        boolean aOngoing =
                                a.getStartDate()
                                        .compareTo(todayDate) <= 0
                                        &&
                                        a.getEndDate()
                                                .compareTo(todayDate) >= 0;

                        boolean bOngoing =
                                b.getStartDate()
                                        .compareTo(todayDate) <= 0
                                        &&
                                        b.getEndDate()
                                                .compareTo(todayDate) >= 0;

                        if(aOngoing && !bOngoing){

                            return -1;

                        }

                        if(!aOngoing && bOngoing){

                            return 1;

                        }

                        return a.getStartDate()
                                .compareTo(
                                        b.getStartDate()
                                );

                    }

            );
            if(festivalList.size() > 6){

                festivalList =
                        festivalList.subList(
                                0,
                                6
                        );

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }

        return festivalList;

    }

    public List<TourDto> getAllFestivalList() {

        List<TourDto> festivalList =
                new ArrayList<>();

        try {

            String currentYear =
                    String.valueOf(
                            LocalDate.now().getYear()
                    );

            String url =
                    "https://apis.data.go.kr/B551011/KorService2/searchFestival2"
                            + "?serviceKey=" + serviceKey
                            + "&MobileOS=ETC"
                            + "&MobileApp=TripMate"
                            + "&_type=json"
                            + "&eventStartDate="
                            + currentYear
                            + "0101"
                            + "&numOfRows=1000"
                            + "&pageNo=1";

            RestTemplate restTemplate =
                    new RestTemplate();

            String response =
                    restTemplate.getForObject(
                            url,
                            String.class
                    );

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

                String image =
                        item.optString(
                                "firstimage",
                                ""
                        );

                if(image.isBlank()){

                    continue;

                }

                String startDate =
                        item.optString(
                                "eventstartdate",
                                ""
                        );

                if(!startDate.startsWith(currentYear)){

                    continue;

                }

                festivalList.add(
                        new TourDto(

                                item.optString(
                                        "title",
                                        ""
                                ),

                                image,

                                item.optString(
                                        "addr1",
                                        ""
                                ),

                                item.optString(
                                        "eventstartdate",
                                        ""
                                ),

                                item.optString(
                                        "eventenddate",
                                        ""
                                )

                        )

                );

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }

        festivalList.sort(

                (a, b) -> {

                    String today =
                            LocalDate.now()
                                    .format(
                                            DateTimeFormatter.ofPattern(
                                                    "yyyyMMdd"
                                            )
                                    );

                    int aPriority;
                    int bPriority;

                    // 진행중
                    if(a.getStartDate().compareTo(today) <= 0 &&
                            a.getEndDate().compareTo(today) >= 0){

                        aPriority = 0;

                    }
                    // 예정
                    else if(a.getStartDate().compareTo(today) > 0){

                        aPriority = 1;

                    }
                    // 종료
                    else{

                        aPriority = 2;

                    }

                    if(b.getStartDate().compareTo(today) <= 0 &&
                            b.getEndDate().compareTo(today) >= 0){

                        bPriority = 0;

                    }
                    else if(b.getStartDate().compareTo(today) > 0){

                        bPriority = 1;

                    }
                    else{

                        bPriority = 2;

                    }

                    if(aPriority != bPriority){

                        return Integer.compare(
                                aPriority,
                                bPriority
                        );

                    }

                    return a.getStartDate()
                            .compareTo(
                                    b.getStartDate()
                            );

                }

        );

        return festivalList;

    }
}