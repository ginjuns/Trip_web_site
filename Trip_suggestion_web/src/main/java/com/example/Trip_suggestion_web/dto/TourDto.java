package com.example.Trip_suggestion_web.dto;

public class TourDto {

    private String title;
    private String image;
    private String address;

    private String startDate;
    private String endDate;

    // 관광지용 생성자
    public TourDto(
            String title,
            String image,
            String address
    ) {

        this.title = title;
        this.image = image;
        this.address = address;

        this.startDate = "";
        this.endDate = "";

    }

    // 축제용 생성자
    public TourDto(
            String title,
            String image,
            String address,
            String startDate,
            String endDate
    ) {

        this.title = title;
        this.image = image;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public String getTitle() {

        return title;

    }

    public String getImage() {

        return image;

    }

    public String getAddress() {

        return address;

    }

    public String getStartDate() {

        return startDate;

    }

    public String getEndDate() {

        return endDate;

    }

    public boolean isFinished(){

        String today =
                java.time.LocalDate.now()
                        .format(
                                java.time.format.DateTimeFormatter
                                        .ofPattern("yyyyMMdd")
                        );

        return endDate.compareTo(today) < 0;
    }

    public String getStatus(){

        String today =
                java.time.LocalDate.now()
                        .format(
                                java.time.format.DateTimeFormatter
                                        .ofPattern("yyyyMMdd")
                        );

        if(endDate.compareTo(today) < 0){

            return "종료";

        }

        if(startDate.compareTo(today) <= 0 &&
                endDate.compareTo(today) >= 0){

            return "진행중";

        }

        return "예정";
    }

}