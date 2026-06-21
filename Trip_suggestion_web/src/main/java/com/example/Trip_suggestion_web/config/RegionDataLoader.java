package com.example.Trip_suggestion_web.config;

import com.example.Trip_suggestion_web.entity.RegionProfile;
import com.example.Trip_suggestion_web.repository.RegionProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 애플리케이션 시작 시 resources/data/region_profiles.csv 를 읽어
 * region_profile 테이블이 비어있으면 226개(+제주/세종 등) 지역 데이터를 적재한다.
 *
 * CSV 컬럼 순서:
 * name,parent,type,area_code,nature,food,festival,activity,
 * solo,couple,friend,family,spring,summer,fall,winter,
 * budget_low,budget_mid,budget_high
 */
@Component
public class RegionDataLoader implements CommandLineRunner {

    private final RegionProfileRepository regionProfileRepository;

    public RegionDataLoader(RegionProfileRepository regionProfileRepository) {
        this.regionProfileRepository = regionProfileRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (regionProfileRepository.count() > 0) {
            return; // 이미 적재됨
        }

        List<RegionProfile> profiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("data/region_profiles.csv").getInputStream(),
                        StandardCharsets.UTF_8))) {

            String line = br.readLine(); // 헤더 스킵
            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] c = line.split(",");

                profiles.add(new RegionProfile(
                        c[0],                      // name
                        c[1],                      // parent
                        c[2],                      // type
                        Integer.parseInt(c[3]),    // areaCode
                        Integer.parseInt(c[4]),    // nature
                        Integer.parseInt(c[5]),    // food
                        Integer.parseInt(c[6]),    // festival
                        Integer.parseInt(c[7]),    // activity
                        Integer.parseInt(c[8]),    // solo
                        Integer.parseInt(c[9]),    // couple
                        Integer.parseInt(c[10]),   // friend
                        Integer.parseInt(c[11]),   // family
                        Integer.parseInt(c[12]),   // spring
                        Integer.parseInt(c[13]),   // summer
                        Integer.parseInt(c[14]),   // fall
                        Integer.parseInt(c[15]),   // winter
                        Integer.parseInt(c[16]),   // budget_low
                        Integer.parseInt(c[17]),   // budget_mid
                        Integer.parseInt(c[18])    // budget_high
                ));
            }
        }

        regionProfileRepository.saveAll(profiles);

        System.out.println("[RegionDataLoader] 지역 프로필 " + profiles.size() + "개 적재 완료");
    }
}