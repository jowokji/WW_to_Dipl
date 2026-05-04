package com.weatherwear.mobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private static final String PREFS = "weatherwear";
    private static final String KEY_TOKEN = "jwt";
    private static final String KEY_BASE_URL = "baseUrl";
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8090/api";

    private ApiClient apiClient;
    private SharedPreferences preferences;
    private String token;
    private String lastWeatherCity = "";
    private Long chatSessionId;

    private EditText baseUrlInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText weatherCityInput;
    private EditText recommendationCityInput;
    private EditText occasionInput;
    private EditText chatMessageInput;

    private TextView statusView;
    private TextView authOutput;
    private TextView weatherOutput;
    private TextView recommendationOutput;
    private TextView chatOutput;
    private TextView historyOutput;

    private LinearLayout loginScreen;
    private LinearLayout weatherScreen;
    private LinearLayout recommendationScreen;
    private LinearLayout chatScreen;
    private LinearLayout historyScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "");
        apiClient = new ApiClient(new Handler(Looper.getMainLooper()));

        buildUi();
        updateStatus();
        showScreen(hasToken() ? weatherScreen : loginScreen);
    }

    private void buildUi() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = verticalLayout();
        root.setPadding(dp(18), dp(18), dp(18), dp(24));
        scrollView.addView(root);

        root.addView(title("WeatherWear MVP"));

        statusView = text("");
        root.addView(statusView);

        baseUrlInput = input("Backend URL", false);
        baseUrlInput.setText(preferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL));
        root.addView(baseUrlInput);

        root.addView(navigation());

        loginScreen = screen("Login/Register");
        weatherScreen = screen("Weather by city");
        recommendationScreen = screen("Recommendation");
        chatScreen = screen("Chat");
        historyScreen = screen("History");

        buildLoginScreen();
        buildWeatherScreen();
        buildRecommendationScreen();
        buildChatScreen();
        buildHistoryScreen();

        root.addView(loginScreen);
        root.addView(weatherScreen);
        root.addView(recommendationScreen);
        root.addView(chatScreen);
        root.addView(historyScreen);

        setContentView(scrollView);
    }

    private View navigation() {
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setPadding(0, dp(8), 0, dp(8));

        navigation.addView(button("Login", () -> showScreen(loginScreen)));
        navigation.addView(button("Weather", () -> showScreen(weatherScreen)));
        navigation.addView(button("Recommendation", () -> showScreen(recommendationScreen)));
        navigation.addView(button("Chat", () -> showScreen(chatScreen)));
        navigation.addView(button("History", () -> showScreen(historyScreen)));

        scrollView.addView(navigation);
        return scrollView;
    }

    private void buildLoginScreen() {
        emailInput = input("Email", false);
        passwordInput = input("Password", true);
        authOutput = output();

        loginScreen.addView(emailInput);
        loginScreen.addView(passwordInput);
        loginScreen.addView(button("Register", this::register));
        loginScreen.addView(button("Login", this::login));
        loginScreen.addView(button("Clear token", this::clearToken));
        loginScreen.addView(authOutput);
    }

    private void buildWeatherScreen() {
        weatherCityInput = input("City", false);
        weatherOutput = output();

        weatherScreen.addView(weatherCityInput);
        weatherScreen.addView(button("Get weather", this::getWeather));
        weatherScreen.addView(weatherOutput);
    }

    private void buildRecommendationScreen() {
        recommendationCityInput = input("City", false);
        occasionInput = input("Occasion, for example walk or work", false);
        recommendationOutput = output();

        recommendationScreen.addView(recommendationCityInput);
        recommendationScreen.addView(occasionInput);
        recommendationScreen.addView(button("Get recommendation", this::getRecommendation));
        recommendationScreen.addView(recommendationOutput);
    }

    private void buildChatScreen() {
        chatMessageInput = input("Message", false);
        chatOutput = output();

        chatScreen.addView(chatMessageInput);
        chatScreen.addView(button("Send", this::sendChatMessage));
        chatScreen.addView(button("New chat", () -> {
            chatSessionId = null;
            chatOutput.setText("New chat started.");
        }));
        chatScreen.addView(chatOutput);
    }

    private void buildHistoryScreen() {
        historyOutput = output();

        historyScreen.addView(button("Load history", this::loadHistory));
        historyScreen.addView(button("Clear history", this::clearHistory));
        historyScreen.addView(historyOutput);
    }

    private void register() {
        postAuth("/auth/register");
    }

    private void login() {
        postAuth("/auth/login");
    }

    private void postAuth(String path) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            authOutput.setText("Enter email and password first.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "email", email);
        put(body, "password", password);

        authOutput.setText("Connecting...");

        api("POST", path, body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    token = json.optString("token", "");
                    if (token.trim().isEmpty()) {
                        authOutput.setText("Authentication response does not contain JWT.");
                        return;
                    }

                    preferences.edit().putString(KEY_TOKEN, token).apply();
                    updateStatus();
                    authOutput.setText("JWT saved for " + json.optString("email"));
                    showScreen(weatherScreen);
                } catch (Exception ex) {
                    authOutput.setText("Authentication response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                authOutput.setText(message);
            }
        });
    }

    private void getWeather() {
        if (!requireToken(weatherOutput)) {
            return;
        }

        String city = weatherCityInput.getText().toString().trim();
        if (city.isEmpty()) {
            weatherOutput.setText("Enter a city first.");
            return;
        }

        weatherOutput.setText("Loading weather...");

        api("GET", "/weather?city=" + encode(city), null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String responseCity = json.optString("city");
                    lastWeatherCity = responseCity.trim().isEmpty() ? city : responseCity;
                    if (recommendationCityInput.getText().toString().trim().isEmpty()) {
                        recommendationCityInput.setText(lastWeatherCity);
                    }

                    String result = "City: " + json.optString("city") + "\n"
                            + "Temperature: " + json.optDouble("temperature") + " C\n"
                            + "Wind: " + json.optDouble("windSpeed") + " m/s\n"
                            + "Humidity: " + json.optInt("humidity") + "%\n"
                            + "Condition: " + json.optString("condition");
                    weatherOutput.setText(result);
                } catch (Exception ex) {
                    weatherOutput.setText("Weather response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                weatherOutput.setText(message);
            }
        });
    }

    private void getRecommendation() {
        if (!requireToken(recommendationOutput)) {
            return;
        }

        String city = recommendationCityInput.getText().toString().trim();
        if (city.isEmpty() && !lastWeatherCity.trim().isEmpty()) {
            city = lastWeatherCity;
            recommendationCityInput.setText(city);
        }

        String occasion = occasionInput.getText().toString().trim();

        if (city.isEmpty()) {
            recommendationOutput.setText("Enter a city first.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "city", city);
        if (!occasion.isEmpty()) {
            put(body, "occasion", occasion);
        }

        recommendationOutput.setText("Getting recommendation...");

        api("POST", "/recommendations", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    recommendationOutput.setText(formatRecommendation(json));
                    loadHistory();
                } catch (Exception ex) {
                    recommendationOutput.setText("Recommendation response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                recommendationOutput.setText(message);
            }
        });
    }

    private void sendChatMessage() {
        if (!requireToken(chatOutput)) {
            return;
        }

        String message = chatMessageInput.getText().toString().trim();
        if (message.isEmpty()) {
            chatOutput.setText("Enter a message first.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "message", message);
        if (chatSessionId != null) {
            put(body, "sessionId", chatSessionId);
        }

        chatOutput.setText("Sending...");

        api("POST", "/chat", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (!json.isNull("sessionId")) {
                        chatSessionId = json.getLong("sessionId");
                    }
                    chatMessageInput.setText("");
                    chatOutput.setText(json.optString("answer"));
                } catch (Exception ex) {
                    chatOutput.setText("Chat response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                chatOutput.setText(message);
            }
        });
    }

    private void loadHistory() {
        if (!requireToken(historyOutput)) {
            return;
        }

        historyOutput.setText("Loading history...");

        api("GET", "/history", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray items = new JSONArray(response);
                    StringBuilder builder = new StringBuilder();

                    for (int index = 0; index < items.length(); index++) {
                        JSONObject item = items.getJSONObject(index);
                        builder.append(item.optString("createdAt"))
                                .append("\n")
                                .append(item.optString("city"))
                                .append("\n")
                                .append(item.optString("weatherSummary"))
                                .append("\n")
                                .append(item.optString("recommendationText"))
                                .append("\n\n");
                    }

                    historyOutput.setText(builder.length() == 0
                            ? "History is empty."
                            : builder.toString());
                } catch (Exception ex) {
                    historyOutput.setText("History response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                historyOutput.setText(message);
            }
        });
    }

    private void clearHistory() {
        if (!requireToken(historyOutput)) {
            return;
        }

        api("DELETE", "/history", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                historyOutput.setText("History cleared.");
            }

            @Override
            public void onError(String message) {
                historyOutput.setText(message);
            }
        });
    }

    private void clearToken() {
        token = "";
        preferences.edit().remove(KEY_TOKEN).apply();
        updateStatus();
        authOutput.setText("Token cleared.");
    }

    private void api(String method, String path, JSONObject body, ApiCallback callback) {
        String requestToken = path.startsWith("/auth/") ? "" : token;
        apiClient.request(
                method,
                currentBaseUrl(),
                path,
                requestToken,
                body,
                callback
        );
    }

    private String currentBaseUrl() {
        String baseUrl = baseUrlInput.getText().toString().trim();
        if (baseUrl.isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
            baseUrlInput.setText(baseUrl);
        }

        preferences.edit()
                .putString(KEY_BASE_URL, baseUrl)
                .apply();

        return baseUrl;
    }

    private void updateStatus() {
        statusView.setText(hasToken() ? "JWT token saved" : "Not authenticated");
    }

    private boolean hasToken() {
        return token != null && !token.trim().isEmpty();
    }

    private boolean requireToken(TextView target) {
        if (hasToken()) {
            return true;
        }

        String message = "Login or register first. JWT is required for this request.";
        target.setText(message);
        authOutput.setText(message);
        showScreen(loginScreen);
        return false;
    }

    private String formatRecommendation(JSONObject json) {
        StringBuilder builder = new StringBuilder();

        String city = json.optString("city");
        if (!city.trim().isEmpty()) {
            builder.append("City: ").append(city).append("\n\n");
        }

        String weatherSummary = json.optString("weatherSummary");
        if (!weatherSummary.trim().isEmpty()) {
            builder.append(weatherSummary).append("\n\n");
        }

        builder.append(json.optString("recommendation"));
        return builder.toString();
    }

    private void showScreen(LinearLayout visibleScreen) {
        loginScreen.setVisibility(visibleScreen == loginScreen ? View.VISIBLE : View.GONE);
        weatherScreen.setVisibility(visibleScreen == weatherScreen ? View.VISIBLE : View.GONE);
        recommendationScreen.setVisibility(
                visibleScreen == recommendationScreen ? View.VISIBLE : View.GONE
        );
        chatScreen.setVisibility(visibleScreen == chatScreen ? View.VISIBLE : View.GONE);
        historyScreen.setVisibility(visibleScreen == historyScreen ? View.VISIBLE : View.GONE);
    }

    private LinearLayout screen(String title) {
        LinearLayout layout = verticalLayout();
        layout.setPadding(0, dp(8), 0, dp(8));
        layout.addView(section(title));
        return layout;
    }

    private LinearLayout verticalLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private TextView title(String value) {
        TextView view = text(value);
        view.setTextSize(28);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    private TextView section(String value) {
        TextView view = text(value);
        view.setTextSize(20);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setPadding(0, dp(12), 0, dp(6));
        return view;
    }

    private TextView text(String value) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(15);
        view.setPadding(0, dp(6), 0, dp(6));
        return view;
    }

    private TextView output() {
        TextView view = text("");
        view.setTypeface(Typeface.MONOSPACE);
        view.setTextIsSelectable(true);
        return view;
    }

    private EditText input(String hint, boolean password) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setSingleLine(!hint.equals("Message"));
        editText.setInputType(password
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                : InputType.TYPE_CLASS_TEXT);
        editText.setPadding(0, dp(8), 0, dp(8));
        return editText;
    }

    private Button button(String text, Runnable action) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setOnClickListener(view -> action.run());
        return button;
    }

    private void put(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (Exception ignored) {
        }
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            return value;
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
