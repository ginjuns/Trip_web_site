package com.example.Trip_suggestion_web.external.weather;

import java.util.HashMap;
import java.util.Map;

/**
 * 날씨 위치 매핑.
 *
 * 1) 동네예보(단기예보) 격자좌표(nx, ny) : 161개 시군구 단위로 정확히 매핑한다.
 *    (기상청 공식 LCC 격자변환 공식으로 각 지역 시청/군청 좌표 -> nx,ny 계산)
 *    -> /weather/calendar, /weather/detail 의 "최근 5일 실제 예보"에 사용.
 *
 * 2) ASOS 관측소(stationId) : 전국 ASOS 관측소는 100여 개뿐이라 시군구 단위로
 *    매핑하기엔 무리가 있어, 기존처럼 상위 광역자치단체(parent) 17개 기준 대표
 *    관측소를 사용한다. (RegionProfile.getParent() 값을 키로 사용)
 *    -> /weather/detail 의 "최근 5년 기후 통계"에 사용.
 */
public class WeatherRegionMapper {

    public record StationInfo(int stationId, int nx, int ny) {}

    // ===== 1) 시군구(지역명) -> nx, ny =====
    private static final Map<String, int[]> REGION_GRID = new HashMap<>();

    static {
        REGION_GRID.put("가평군", new int[]{69, 133});
        REGION_GRID.put("강릉시", new int[]{92, 132});
        REGION_GRID.put("강진군", new int[]{57, 63});
        REGION_GRID.put("거제시", new int[]{90, 69});
        REGION_GRID.put("거창군", new int[]{77, 86});
        REGION_GRID.put("경산시", new int[]{91, 90});
        REGION_GRID.put("경주시", new int[]{100, 91});
        REGION_GRID.put("계룡시", new int[]{65, 99});
        REGION_GRID.put("고령군", new int[]{83, 87});
        REGION_GRID.put("고성군(강원)", new int[]{85, 145});
        REGION_GRID.put("고성군(경남)", new int[]{85, 71});
        REGION_GRID.put("고양시", new int[]{57, 129});
        REGION_GRID.put("고창군", new int[]{55, 80});
        REGION_GRID.put("고흥군", new int[]{66, 62});
        REGION_GRID.put("곡성군", new int[]{66, 77});
        REGION_GRID.put("공주시", new int[]{63, 102});
        REGION_GRID.put("과천시", new int[]{60, 124});
        REGION_GRID.put("광명시", new int[]{58, 125});
        REGION_GRID.put("광양시", new int[]{73, 70});
        REGION_GRID.put("광주광역시", new int[]{58, 74});
        REGION_GRID.put("광주시(경기)", new int[]{65, 124});
        REGION_GRID.put("괴산군", new int[]{74, 111});
        REGION_GRID.put("구례군", new int[]{69, 75});
        REGION_GRID.put("구리시", new int[]{62, 127});
        REGION_GRID.put("구미시", new int[]{84, 96});
        REGION_GRID.put("군산시", new int[]{56, 92});
        REGION_GRID.put("군포시", new int[]{59, 122});
        REGION_GRID.put("금산군", new int[]{69, 95});
        REGION_GRID.put("김제시", new int[]{59, 88});
        REGION_GRID.put("김천시", new int[]{80, 96});
        REGION_GRID.put("김포시", new int[]{55, 128});
        REGION_GRID.put("김해시", new int[]{94, 77});
        REGION_GRID.put("나주시", new int[]{56, 71});
        REGION_GRID.put("남양주시", new int[]{64, 128});
        REGION_GRID.put("남원시", new int[]{68, 80});
        REGION_GRID.put("남해군", new int[]{77, 68});
        REGION_GRID.put("논산시", new int[]{62, 97});
        REGION_GRID.put("단양군", new int[]{84, 115});
        REGION_GRID.put("담양군", new int[]{61, 78});
        REGION_GRID.put("당진시", new int[]{54, 112});
        REGION_GRID.put("대구광역시", new int[]{89, 91});
        REGION_GRID.put("대전광역시", new int[]{67, 100});
        REGION_GRID.put("동두천시", new int[]{61, 134});
        REGION_GRID.put("동해시", new int[]{97, 127});
        REGION_GRID.put("목포시", new int[]{50, 67});
        REGION_GRID.put("무안군", new int[]{52, 71});
        REGION_GRID.put("무주군", new int[]{72, 93});
        REGION_GRID.put("문경시", new int[]{81, 106});
        REGION_GRID.put("밀양시", new int[]{92, 83});
        REGION_GRID.put("보령시", new int[]{54, 100});
        REGION_GRID.put("보성군", new int[]{62, 66});
        REGION_GRID.put("보은군", new int[]{73, 104});
        REGION_GRID.put("봉화군", new int[]{90, 113});
        REGION_GRID.put("부산광역시", new int[]{98, 76});
        REGION_GRID.put("부안군", new int[]{56, 87});
        REGION_GRID.put("부여군", new int[]{59, 99});
        REGION_GRID.put("부천시", new int[]{56, 125});
        REGION_GRID.put("사천시", new int[]{80, 71});
        REGION_GRID.put("산청군", new int[]{76, 80});
        REGION_GRID.put("삼척시", new int[]{97, 125});
        REGION_GRID.put("상주시", new int[]{81, 102});
        REGION_GRID.put("서귀포시", new int[]{53, 33});
        REGION_GRID.put("서산시", new int[]{51, 110});
        REGION_GRID.put("서울특별시", new int[]{60, 127});
        REGION_GRID.put("서천군", new int[]{55, 94});
        REGION_GRID.put("성남시", new int[]{62, 124});
        REGION_GRID.put("성주군", new int[]{83, 91});
        REGION_GRID.put("세종특별자치시", new int[]{66, 103});
        REGION_GRID.put("속초시", new int[]{87, 141});
        REGION_GRID.put("수원시", new int[]{61, 120});
        REGION_GRID.put("순창군", new int[]{63, 79});
        REGION_GRID.put("순천시", new int[]{70, 70});
        REGION_GRID.put("시흥시", new int[]{57, 123});
        REGION_GRID.put("신안군", new int[]{49, 67});
        REGION_GRID.put("아산시", new int[]{60, 110});
        REGION_GRID.put("안동시", new int[]{91, 106});
        REGION_GRID.put("안산시", new int[]{57, 121});
        REGION_GRID.put("안성시", new int[]{65, 115});
        REGION_GRID.put("안양시", new int[]{59, 123});
        REGION_GRID.put("양구군", new int[]{77, 139});
        REGION_GRID.put("양산시", new int[]{97, 79});
        REGION_GRID.put("양양군", new int[]{88, 138});
        REGION_GRID.put("양주시", new int[]{61, 131});
        REGION_GRID.put("양평군", new int[]{69, 125});
        REGION_GRID.put("여수시", new int[]{73, 66});
        REGION_GRID.put("여주시", new int[]{71, 121});
        REGION_GRID.put("연천군", new int[]{61, 138});
        REGION_GRID.put("영광군", new int[]{52, 77});
        REGION_GRID.put("영덕군", new int[]{102, 103});
        REGION_GRID.put("영동군", new int[]{74, 97});
        REGION_GRID.put("영암군", new int[]{55, 66});
        REGION_GRID.put("영양군", new int[]{97, 108});
        REGION_GRID.put("영월군", new int[]{86, 119});
        REGION_GRID.put("영주시", new int[]{89, 111});
        REGION_GRID.put("영천시", new int[]{95, 93});
        REGION_GRID.put("예산군", new int[]{58, 107});
        REGION_GRID.put("예천군", new int[]{86, 108});
        REGION_GRID.put("오산시", new int[]{62, 118});
        REGION_GRID.put("옥천군", new int[]{71, 100});
        REGION_GRID.put("완도군", new int[]{57, 56});
        REGION_GRID.put("완주군", new int[]{64, 91});
        REGION_GRID.put("용인시", new int[]{63, 120});
        REGION_GRID.put("울릉군", new int[]{127, 127});
        REGION_GRID.put("울산광역시", new int[]{102, 84});
        REGION_GRID.put("울진군", new int[]{102, 115});
        REGION_GRID.put("원주시", new int[]{76, 122});
        REGION_GRID.put("음성군", new int[]{72, 113});
        REGION_GRID.put("의령군", new int[]{83, 78});
        REGION_GRID.put("의성군", new int[]{90, 101});
        REGION_GRID.put("의왕시", new int[]{60, 122});
        REGION_GRID.put("의정부시", new int[]{61, 130});
        REGION_GRID.put("이천시", new int[]{68, 120});
        REGION_GRID.put("익산시", new int[]{60, 92});
        REGION_GRID.put("인제군", new int[]{80, 138});
        REGION_GRID.put("인천광역시", new int[]{55, 124});
        REGION_GRID.put("임실군", new int[]{66, 84});
        REGION_GRID.put("장성군", new int[]{57, 77});
        REGION_GRID.put("장수군", new int[]{70, 85});
        REGION_GRID.put("장흥군", new int[]{59, 64});
        REGION_GRID.put("전주시", new int[]{63, 89});
        REGION_GRID.put("정선군", new int[]{89, 123});
        REGION_GRID.put("정읍시", new int[]{58, 83});
        REGION_GRID.put("제주시", new int[]{53, 38});
        REGION_GRID.put("제천시", new int[]{81, 118});
        REGION_GRID.put("증평군", new int[]{71, 110});
        REGION_GRID.put("진도군", new int[]{48, 60});
        REGION_GRID.put("진안군", new int[]{68, 88});
        REGION_GRID.put("진주시", new int[]{81, 75});
        REGION_GRID.put("진천군", new int[]{68, 111});
        REGION_GRID.put("창녕군", new int[]{87, 83});
        REGION_GRID.put("창원시", new int[]{91, 77});
        REGION_GRID.put("천안시", new int[]{62, 110});
        REGION_GRID.put("철원군", new int[]{65, 139});
        REGION_GRID.put("청도군", new int[]{91, 86});
        REGION_GRID.put("청송군", new int[]{96, 103});
        REGION_GRID.put("청양군", new int[]{57, 103});
        REGION_GRID.put("청주시", new int[]{69, 107});
        REGION_GRID.put("춘천시", new int[]{73, 134});
        REGION_GRID.put("충주시", new int[]{76, 115});
        REGION_GRID.put("칠곡군", new int[]{85, 93});
        REGION_GRID.put("태백시", new int[]{95, 119});
        REGION_GRID.put("태안군", new int[]{48, 109});
        REGION_GRID.put("통영시", new int[]{87, 68});
        REGION_GRID.put("파주시", new int[]{56, 131});
        REGION_GRID.put("평창군", new int[]{84, 123});
        REGION_GRID.put("평택시", new int[]{62, 114});
        REGION_GRID.put("포천시", new int[]{64, 134});
        REGION_GRID.put("포항시", new int[]{102, 94});
        REGION_GRID.put("하남시", new int[]{64, 126});
        REGION_GRID.put("하동군", new int[]{74, 73});
        REGION_GRID.put("함안군", new int[]{86, 77});
        REGION_GRID.put("함양군", new int[]{74, 82});
        REGION_GRID.put("함평군", new int[]{52, 72});
        REGION_GRID.put("합천군", new int[]{81, 84});
        REGION_GRID.put("해남군", new int[]{54, 61});
        REGION_GRID.put("홍성군", new int[]{55, 106});
        REGION_GRID.put("홍천군", new int[]{75, 130});
        REGION_GRID.put("화성시", new int[]{57, 119});
        REGION_GRID.put("화순군", new int[]{61, 72});
        REGION_GRID.put("화천군", new int[]{72, 139});
        REGION_GRID.put("횡성군", new int[]{78, 125});
    }

    // ===== 2) 광역자치단체(parent) -> ASOS stationId, 대표 nx, ny (폴백용) =====
    private static final Map<String, StationInfo> PARENT_MAP = new HashMap<>();

    static {
        PARENT_MAP.put("서울특별시",      new StationInfo(108, 60, 127));
        PARENT_MAP.put("부산광역시",      new StationInfo(159, 98, 76));
        PARENT_MAP.put("대구광역시",      new StationInfo(143, 89, 90));
        PARENT_MAP.put("인천광역시",      new StationInfo(112, 55, 124));
        PARENT_MAP.put("광주광역시",      new StationInfo(156, 58, 74));
        PARENT_MAP.put("대전광역시",      new StationInfo(133, 67, 100));
        PARENT_MAP.put("울산광역시",      new StationInfo(152, 102, 84));
        PARENT_MAP.put("세종특별자치시",   new StationInfo(239, 66, 103));
        PARENT_MAP.put("경기도",         new StationInfo(119, 60, 121));
        PARENT_MAP.put("강원특별자치도",   new StationInfo(101, 73, 134));
        PARENT_MAP.put("충청북도",       new StationInfo(131, 69, 107));
        PARENT_MAP.put("충청남도",       new StationInfo(232, 55, 113));
        PARENT_MAP.put("경상북도",       new StationInfo(136, 91, 106));
        PARENT_MAP.put("경상남도",       new StationInfo(155, 90, 77));
        PARENT_MAP.put("전북특별자치도",   new StationInfo(146, 63, 89));
        PARENT_MAP.put("전라남도",       new StationInfo(165, 50, 67));
        PARENT_MAP.put("제주특별자치도",   new StationInfo(184, 52, 38));
    }

    /**
     * 지역명(예: "가평군", "서울특별시") 기준으로 nx,ny를 정확히 찾고,
     * ASOS stationId는 상위 광역자치단체(parentProvince) 기준 대표 관측소를 사용한다.
     *
     * @param regionName      RegionProfile.getName() 값 (세션의 "destination")
     * @param parentProvince  RegionProfile.getParent() 값 (세션의 "destinationProvince")
     */
    public static StationInfo resolve(String regionName, String parentProvince) {

        StationInfo parentInfo =
                PARENT_MAP.getOrDefault(parentProvince, PARENT_MAP.get("서울특별시"));

        int[] grid = (regionName != null) ? REGION_GRID.get(regionName) : null;

        if (grid != null) {
            // 시군구 단위 정확한 nx,ny + 광역 단위 대표 ASOS 관측소
            return new StationInfo(parentInfo.stationId(), grid[0], grid[1]);
        }

        // 매핑에 없는 지역명이면 기존처럼 광역자치단체 대표 좌표로 폴백
        return parentInfo;
    }

    /** 기존 호출부 호환용 (지역명 없이 광역자치단체만 아는 경우) */
    public static StationInfo resolve(String parentProvince) {
        return PARENT_MAP.getOrDefault(parentProvince, PARENT_MAP.get("서울특별시"));
    }
}