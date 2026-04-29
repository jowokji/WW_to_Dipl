package com.weatherwear.mapper;

import com.weatherwear.client.weather.OpenWeatherResponse;
import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.ChatRole;
import com.weatherwear.common.Role;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.auth.AuthResponse;
import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.dto.chat.ChatSessionResponse;
import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.ChatMessage;
import com.weatherwear.entity.ChatSession;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserPreference;
import com.weatherwear.entity.WeatherCache;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class MapperTest {

    private final ChatMapper chatMapper = new ChatMapper();
    private final PreferenceMapper preferenceMapper = new PreferenceMapper();
    private final RecommendationMapper recommendationMapper = new RecommendationMapper();
    private final UserMapper userMapper = new UserMapper();
    private final WeatherMapper weatherMapper = new WeatherMapper();

    @Test
    void chatMapper_mapsMessageSessionResponseAndEntities() {
        User user = user();
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 29, 12, 0);
        ChatSession session = ChatSession.builder()
                .id(1L)
                .user(user)
                .title("Style chat")
                .createdAt(createdAt)
                .updatedAt(createdAt.plusMinutes(5))
                .build();
        ChatMessage assistantMessage = ChatMessage.builder()
                .id(2L)
                .session(session)
                .role(ChatRole.ASSISTANT)
                .content("Wear a jacket")
                .createdAt(createdAt.plusMinutes(1))
                .build();

        ChatMessageDto messageDto = chatMapper.toMessageDto(assistantMessage);
        ChatSessionResponse sessionResponse = chatMapper.toSessionResponse(session);
        ChatResponse chatResponse = chatMapper.toChatResponse(session, assistantMessage);
        ChatSession newSession = chatMapper.toSession(user, "New chat");
        ChatMessage userMessage = chatMapper.toUserMessage(session, "Hi");
        ChatMessage mappedAssistant = chatMapper.toAssistantMessage(session, "Hello");

        assertThat(messageDto.getId()).isEqualTo(2L);
        assertThat(messageDto.getRole()).isEqualTo(ChatRole.ASSISTANT);
        assertThat(sessionResponse.getId()).isEqualTo(1L);
        assertThat(sessionResponse.getTitle()).isEqualTo("Style chat");
        assertThat(chatResponse.getSessionId()).isEqualTo(1L);
        assertThat(chatResponse.getAnswer()).isEqualTo("Wear a jacket");
        assertThat(newSession.getUser()).isSameAs(user);
        assertThat(newSession.getTitle()).isEqualTo("New chat");
        assertThat(userMessage.getRole()).isEqualTo(ChatRole.USER);
        assertThat(userMessage.getContent()).isEqualTo("Hi");
        assertThat(mappedAssistant.getRole()).isEqualTo(ChatRole.ASSISTANT);
        assertThat(mappedAssistant.getContent()).isEqualTo("Hello");
    }

    @Test
    void preferenceMapper_mapsPreferenceToResponse() {
        UserPreference preference = UserPreference.builder()
                .id(10L)
                .stylePreference(StylePreference.BUSINESS)
                .coldSensitivity(SensitivityLevel.HIGH)
                .heatSensitivity(SensitivityLevel.LOW)
                .windSensitivity(SensitivityLevel.HIGH)
                .rainSensitivity(SensitivityLevel.MEDIUM)
                .maxLayers((short) 4)
                .prefersHeadwear(true)
                .prefersWaterproof(true)
                .activityLevel(ActivityLevel.HIGH)
                .preferredColors("navy")
                .avoidItems("shorts")
                .build();

        PreferenceResponse response = preferenceMapper.toResponse(preference);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getStylePreference()).isEqualTo(StylePreference.BUSINESS);
        assertThat(response.getColdSensitivity()).isEqualTo(SensitivityLevel.HIGH);
        assertThat(response.getActivityLevel()).isEqualTo(ActivityLevel.HIGH);
        assertThat(response.getPreferredColors()).isEqualTo("navy");
        assertThat(response.getAvoidItems()).isEqualTo("shorts");
    }

    @Test
    void recommendationMapper_mapsWeatherHistoryAndNullWeather() {
        User user = user();
        WeatherResponse weather = weatherResponse();
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 29, 12, 0);
        RecommendationHistory history = RecommendationHistory.builder()
                .id(20L)
                .user(user)
                .city("Vilnius")
                .weatherSummary("Temp: 12.0, Feels: 10.0, Condition: CLOUDS")
                .recommendationText("Wear a jacket")
                .createdAt(createdAt)
                .build();

        RecommendationResponse fromWeather = recommendationMapper.toResponse(weather, "Wear a jacket");
        RecommendationResponse fromHistory = recommendationMapper.toResponse(history);
        HistoryResponse historyResponse = recommendationMapper.toHistoryResponse(history);
        RecommendationHistory mappedHistory = recommendationMapper.toHistory(user, weather, "Wear boots");

        assertThat(fromWeather.getCity()).isEqualTo("Vilnius");
        assertThat(fromWeather.getWeatherSummary())
                .isEqualTo("Temp: 12.0, Feels: 10.0, Condition: CLOUDS");
        assertThat(fromHistory.getRecommendation()).isEqualTo("Wear a jacket");
        assertThat(historyResponse.getId()).isEqualTo(20L);
        assertThat(historyResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(mappedHistory.getUser()).isSameAs(user);
        assertThat(mappedHistory.getRecommendationText()).isEqualTo("Wear boots");
        assertThat(recommendationMapper.buildWeatherSummary(null))
                .isEqualTo("Weather data unavailable");
    }

    @Test
    void userMapper_mapsUserToAuthResponse() {
        AuthResponse response = userMapper.toAuthResponse(user(), "jwt-token");

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER.name());
    }

    @Test
    void weatherMapper_mapsApiResponseToCacheAndResponse() {
        OpenWeatherResponse apiResponse = openWeatherResponse("Rain");

        WeatherCache cache = weatherMapper.toCache(apiResponse, 30);
        WeatherResponse response = weatherMapper.toResponse(cache, false);

        assertThat(cache.getCity()).isEqualTo("Vilnius");
        assertThat(cache.getLatitude()).isEqualTo(54.6872);
        assertThat(cache.getLongitude()).isEqualTo(25.2797);
        assertThat(cache.getTemperature()).isEqualTo(12.0);
        assertThat(cache.getFeelsLike()).isEqualTo(10.0);
        assertThat(cache.getHumidity()).isEqualTo(70);
        assertThat(cache.getWindSpeed()).isEqualTo(4.5);
        assertThat(cache.getCondition()).isEqualTo(WeatherCondition.RAIN);
        assertThat(cache.getPrecipitation()).isEqualTo(1.2);
        assertThat(cache.getExpiresAt()).isAfter(cache.getCachedAt());
        assertThat(response.getCity()).isEqualTo("Vilnius");
        assertThat(response.isCached()).isFalse();
    }

    @Test
    void weatherMapper_usesDefaultsForPartialApiResponse() {
        OpenWeatherResponse apiResponse = new OpenWeatherResponse();
        apiResponse.setName(" ");

        WeatherCache cache = weatherMapper.toCache(apiResponse, 30);

        assertThat(cache.getCity()).isEqualTo("Unknown");
        assertThat(cache.getCondition()).isEqualTo(WeatherCondition.UNKNOWN);
        assertThat(cache.getPrecipitation()).isEqualTo(0.0);
    }

    @Test
    void weatherMapper_rejectsNullApiResponse() {
        assertThatNullPointerException()
                .isThrownBy(() -> weatherMapper.toCache(null, 30))
                .withMessage("OpenWeather response must not be null");
    }

    private User user() {
        return User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    private WeatherResponse weatherResponse() {
        return new WeatherResponse(
                "Vilnius",
                54.6872,
                25.2797,
                12.0,
                10.0,
                70,
                4.5,
                WeatherCondition.CLOUDS,
                0.0,
                false
        );
    }

    private OpenWeatherResponse openWeatherResponse(String condition) {
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setName("Vilnius");

        OpenWeatherResponse.Coord coord = new OpenWeatherResponse.Coord();
        coord.setLat(54.6872);
        coord.setLon(25.2797);
        response.setCoord(coord);

        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(12.0);
        main.setFeelsLike(10.0);
        main.setHumidity(70);
        response.setMain(main);

        OpenWeatherResponse.Wind wind = new OpenWeatherResponse.Wind();
        wind.setSpeed(4.5);
        response.setWind(wind);

        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setMain(condition);
        response.setWeather(List.of(weather));

        OpenWeatherResponse.Precipitation rain = new OpenWeatherResponse.Precipitation();
        rain.setOneHour(1.2);
        response.setRain(rain);

        return response;
    }
}
