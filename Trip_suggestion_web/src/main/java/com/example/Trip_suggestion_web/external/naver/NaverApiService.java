package com.example.Trip_suggestion_web.external.naver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class NaverApiService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public List<NewsDto> getNews(String keyword) {

        List<NewsDto> newsList =
                new ArrayList<>();

        try {

            String encodedKeyword =
                    URLEncoder.encode(
                            keyword,
                            StandardCharsets.UTF_8
                    );

            String apiUrl =
                    "https://openapi.naver.com/v1/search/blog.json?query="
                            + encodedKeyword
                            + "&display=5";

            URL url =
                    new URL(apiUrl);

            HttpURLConnection con =
                    (HttpURLConnection)
                            url.openConnection();

            con.setRequestMethod(
                    "GET"
            );

            con.setRequestProperty(
                    "X-Naver-Client-Id",
                    clientId
            );

            con.setRequestProperty(
                    "X-Naver-Client-Secret",
                    clientSecret
            );

            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(
                                    con.getInputStream()
                            )
                    );

            StringBuilder response =
                    new StringBuilder();

            String line;

            while ((line = br.readLine()) != null) {

                response.append(line);

            }

            br.close();

            JSONObject json =
                    new JSONObject(
                            response.toString()
                    );

            JSONArray items =
                    json.getJSONArray(
                            "items"
                    );

            for (int i = 0;
                 i < items.length();
                 i++) {

                JSONObject item =
                        items.getJSONObject(i);

                String title =
                        item.getString(
                                        "title"
                                )
                                .replaceAll(
                                        "<.*?>",
                                        ""
                                );

                String link =
                        item.getString(
                                "link"
                        );

                newsList.add(
                        new NewsDto(
                                title,
                                link
                        )
                );

            }

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return newsList;

    }

}