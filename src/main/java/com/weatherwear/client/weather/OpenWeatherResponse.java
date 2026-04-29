package com.weatherwear.client.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenWeatherResponse {

    private String name;
    private Integer cod;
    private String message;
    private Long dt;
    private Integer timezone;
    private Coord coord;
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Sys sys;
    private List<Weather> weather;
    private Precipitation rain;
    private Precipitation snow;

    @Getter
    @Setter
    public static class Coord {
        private Double lat;
        private Double lon;
    }

    @Getter
    @Setter
    public static class Main {
        private Double temp;

        @JsonProperty("feels_like")
        private Double feelsLike;

        @JsonProperty("temp_min")
        private Double tempMin;

        @JsonProperty("temp_max")
        private Double tempMax;

        private Integer pressure;
        private Integer humidity;
    }

    @Getter
    @Setter
    public static class Wind {
        private Double speed;
        private Integer deg;
        private Double gust;
    }

    @Getter
    @Setter
    public static class Clouds {
        private Integer all;
    }

    @Getter
    @Setter
    public static class Sys {
        private String country;
        private Long sunrise;
        private Long sunset;
    }

    @Getter
    @Setter
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }

    @Getter
    @Setter
    public static class Precipitation {
        @JsonProperty("1h")
        private Double oneHour;
    }
}
