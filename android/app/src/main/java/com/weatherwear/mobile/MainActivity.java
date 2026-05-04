package com.weatherwear.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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

    private static final int LOCATION_PERMISSION_REQUEST = 44;
    private static final String PREFS = "weatherwear";
    private static final String KEY_TOKEN = "jwt";
    private static final String KEY_BASE_URL = "baseUrl";
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8090/api";

    private ApiClient apiClient;
    private SharedPreferences preferences;

    private EditText baseUrlInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText cityInput;
    private EditText occasionInput;
    private EditText chatCityInput;
    private EditText chatMessageInput;
    private TextView statusView;
    private TextView outputView;
    private LinearLayout authPanel;
    private LinearLayout forecastPanel;
    private LinearLayout chatPanel;
    private LinearLayout historyPanel;
    private Long chatSessionId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "");
        apiClient = new ApiClient(new Handler(Looper.getMainLooper()));

        buildUi();
        updateStatus();
        showPanel(authPanel);
    }

    private void buildUi() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(24));
        scrollView.addView(root);

        TextView title = title("WeatherWear");
        root.addView(title);

        statusView = label("");
        root.addView(statusView);

        baseUrlInput = input("Backend URL", false);
        baseUrlInput.setText(preferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL));
        root.addView(baseUrlInput);

        root.addView(navigation());

        authPanel = panel();
        forecastPanel = panel();
        chatPanel = panel();
        historyPanel = panel();

        buildAuthPanel();
        buildForecastPanel();
        buildChatPanel();
        buildHistoryPanel();

        root.addView(authPanel);
        root.addView(forecastPanel);
        root.addView(chatPanel);
        root.addView(historyPanel);

        outputView = label("");
        outputView.setTypeface(Typeface.MONOSPACE);
        outputView.setTextIsSelectable(true);
        root.addView(outputView);

        setContentView(scrollView);
    }

    private View navigation() {
        HorizontalScrollView scroller = new HorizontalScrollView(this);
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setPadding(0, dp(8), 0, dp(8));

        navigation.addView(button("Auth", () -> showPanel(authPanel)));
        navigation.addView(button("Forecast", () -> showPanel(forecastPanel)));
        navigation.addView(button("Chat", () -> showPanel(chatPanel)));
        navigation.addView(button("History", () -> showPanel(historyPanel)));
        navigation.addView(button("Delete account", this::deleteAccount));

        scroller.addView(navigation);
        return scroller;
    }

    private void buildAuthPanel() {
        authPanel.addView(section("Account"));
        emailInput = input("Email", false);
        passwordInput = input("Password", true);
        authPanel.addView(emailInput);
        authPanel.addView(passwordInput);
        authPanel.addView(button("Register", this::register));
        authPanel.addView(button("Login", this::login));
        authPanel.addView(button("Clear token", this::clearToken));
    }

    private void buildForecastPanel() {
        forecastPanel.addView(section("Forecast and recommendation"));
        cityInput = input("City, for example Vilnius", false);
        occasionInput = input("Occasion, for example work or walk", false);
        forecastPanel.addView(cityInput);
        forecastPanel.addView(occasionInput);
        forecastPanel.addView(button("Get weather by city", this::getWeatherByCity));
        forecastPanel.addView(button("Use current location", this::getWeatherByLocation));
        forecastPanel.addView(button("Generate recommendation", this::recommend));
    }

    private void buildChatPanel() {
        chatPanel.addView(section("Style chat"));
        chatCityInput = input("Optional city context", false);
        chatMessageInput = input("Message", false);
        chatPanel.addView(chatCityInput);
        chatPanel.addView(chatMessageInput);
        chatPanel.addView(button("Send message", this::sendChatMessage));
        chatPanel.addView(button("New chat", () -> {
            chatSessionId = null;
            output("Started a new chat session.");
        }));
    }

    private void buildHistoryPanel() {
        historyPanel.addView(section("History"));
        historyPanel.addView(button("Load recommendation history", this::loadHistory));
    }

    private void register() {
        JSONObject body = authBody();
        postAuth("/auth/register", body);
    }

    private void login() {
        JSONObject body = authBody();
        postAuth("/auth/login", body);
    }

    private JSONObject authBody() {
        JSONObject body = new JSONObject();
        put(body, "email", emailInput.getText().toString().trim());
        put(body, "password", passwordInput.getText().toString());
        return body;
    }

    private void postAuth(String path, JSONObject body) {
        saveBaseUrl();
        output("Connecting...");

        api("POST", path, body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    token = json.optString("token", "");
                    preferences.edit().putString(KEY_TOKEN, token).apply();
                    updateStatus();
                    output("Authenticated as " + json.optString("email"));
                } catch (Exception ex) {
                    output("Authentication response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                output(message);
            }
        });
    }

    private void getWeatherByCity() {
        String city = cityInput.getText().toString().trim();
        if (city.isEmpty()) {
            output("Enter a city first.");
            return;
        }

        String path = "/weather?city=" + encode(city);
        api("GET", path, null, simpleJsonOutput("Weather"));
    }

    private void getWeatherByLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        fetchWeatherFromLastKnownLocation();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchWeatherFromLastKnownLocation();
        } else {
            output("Location permission was not granted.");
        }
    }

    private void fetchWeatherFromLastKnownLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        try {
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (SecurityException ex) {
            output("Location permission is required.");
            return;
        }

        if (location == null) {
            output("No last known location is available yet.");
            return;
        }

        String path = "/weather/coordinates?lat="
                + location.getLatitude()
                + "&lon="
                + location.getLongitude();
        api("GET", path, null, simpleJsonOutput("Weather"));
    }

    private void recommend() {
        JSONObject body = new JSONObject();
        String city = cityInput.getText().toString().trim();
        String occasion = occasionInput.getText().toString().trim();

        if (city.isEmpty()) {
            output("Enter a city for the recommendation.");
            return;
        }

        put(body, "city", city);
        if (!occasion.isEmpty()) {
            put(body, "occasion", occasion);
        }

        api("POST", "/recommendations", body, simpleJsonOutput("Recommendation"));
    }

    private void sendChatMessage() {
        String message = chatMessageInput.getText().toString().trim();
        if (message.isEmpty()) {
            output("Enter a chat message first.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "message", message);

        if (chatSessionId != null) {
            put(body, "sessionId", chatSessionId);
        }

        String city = chatCityInput.getText().toString().trim();
        if (!city.isEmpty()) {
            put(body, "city", city);
        }

        api("POST", "/chat", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    chatSessionId = json.optLong("sessionId");
                    output("Assistant:\n" + json.optString("answer"));
                } catch (Exception ex) {
                    output("Chat response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                output(message);
            }
        });
    }

    private void loadHistory() {
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
                                .append(": ")
                                .append(item.optString("recommendationText"))
                                .append("\n\n");
                    }

                    output(builder.length() == 0 ? "History is empty." : builder.toString());
                } catch (Exception ex) {
                    output("History response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                output(message);
            }
        });
    }

    private void deleteAccount() {
        api("DELETE", "/users/me", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                clearToken();
                output("Account and associated data were deleted.");
            }

            @Override
            public void onError(String message) {
                output(message);
            }
        });
    }

    private void clearToken() {
        token = "";
        preferences.edit().remove(KEY_TOKEN).apply();
        updateStatus();
        output("Token cleared.");
    }

    private void api(String method, String path, JSONObject body, ApiCallback callback) {
        saveBaseUrl();
        apiClient.request(
                method,
                baseUrlInput.getText().toString(),
                path,
                token,
                body,
                callback
        );
    }

    private ApiCallback simpleJsonOutput(String label) {
        return new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                output(label + ":\n" + response);
            }

            @Override
            public void onError(String message) {
                output(message);
            }
        };
    }

    private void saveBaseUrl() {
        preferences.edit()
                .putString(KEY_BASE_URL, baseUrlInput.getText().toString().trim())
                .apply();
    }

    private void updateStatus() {
        boolean authenticated = token != null && !token.trim().isEmpty();
        statusView.setText(authenticated ? "JWT token saved" : "Not authenticated");
    }

    private void showPanel(LinearLayout visiblePanel) {
        authPanel.setVisibility(visiblePanel == authPanel ? View.VISIBLE : View.GONE);
        forecastPanel.setVisibility(visiblePanel == forecastPanel ? View.VISIBLE : View.GONE);
        chatPanel.setVisibility(visiblePanel == chatPanel ? View.VISIBLE : View.GONE);
        historyPanel.setVisibility(visiblePanel == historyPanel ? View.VISIBLE : View.GONE);
    }

    private LinearLayout panel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(0, dp(8), 0, dp(8));
        return panel;
    }

    private TextView title(String text) {
        TextView view = label(text);
        view.setTextSize(28);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    private TextView section(String text) {
        TextView view = label(text);
        view.setTextSize(20);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setPadding(0, dp(12), 0, dp(6));
        return view;
    }

    private TextView label(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(15);
        view.setPadding(0, dp(6), 0, dp(6));
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

    private void output(String text) {
        outputView.setText(text == null ? "" : text);
    }

    private void put(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (Exception ignored) {
            output("Failed to build request.");
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
