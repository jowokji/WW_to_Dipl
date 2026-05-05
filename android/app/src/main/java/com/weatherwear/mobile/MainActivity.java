package com.weatherwear.mobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    private static final int COLOR_BACKGROUND = Color.rgb(243, 244, 246);
    private static final int COLOR_SURFACE = Color.WHITE;
    private static final int COLOR_OUTPUT_SURFACE = Color.rgb(250, 251, 252);
    private static final int COLOR_PRIMARY = Color.rgb(47, 52, 58);
    private static final int COLOR_PRIMARY_DARK = Color.rgb(31, 36, 42);
    private static final int COLOR_TEXT = Color.rgb(31, 35, 40);
    private static final int COLOR_MUTED = Color.rgb(104, 112, 121);
    private static final int COLOR_BORDER = Color.rgb(218, 224, 231);
    private static final int COLOR_SOFT_BORDER = Color.rgb(232, 236, 241);
    private static final int COLOR_FIELD = Color.WHITE;
    private static final int COLOR_DANGER = Color.rgb(137, 82, 82);
    private static final int COLOR_SUCCESS = Color.rgb(84, 94, 105);

    private ApiClient apiClient;
    private SharedPreferences preferences;
    private String token;
    private String lastWeatherCity = "";
    private Long chatSessionId;

    private TextView statusChip;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText weatherCityInput;
    private EditText recommendationCityInput;
    private EditText occasionInput;
    private EditText chatMessageInput;

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

    private Button loginTab;
    private Button weatherTab;
    private Button recommendationTab;
    private Button chatTab;
    private Button historyTab;

    private Button registerButton;
    private Button loginButton;
    private Button weatherButton;
    private Button recommendationButton;
    private Button chatSendButton;
    private Button historyLoadButton;
    private Button historyClearButton;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "");
        apiClient = new ApiClient(new Handler(Looper.getMainLooper()));

        applySystemBars();
        buildUi();
        updateStatus();
        showScreen(hasToken() ? weatherScreen : loginScreen);
    }

    private void buildUi() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(COLOR_BACKGROUND);

        LinearLayout root = verticalLayout();
        root.setPadding(dp(18), dp(30), dp(18), dp(24));
        scrollView.addView(root);

        root.addView(appHeader());
        root.addView(navigation());

        loginScreen = screen("Account");
        weatherScreen = screen("Weather");
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

    private View appHeader() {
        LinearLayout header = verticalLayout();
        header.setPadding(0, dp(2), 0, dp(8));

        TextView title = text("WeatherWear");
        title.setTextSize(30);
        title.setTextColor(COLOR_TEXT);
        title.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        title.setIncludeFontPadding(false);
        header.addView(title);

        LinearLayout statusRow = new LinearLayout(this);
        statusRow.setOrientation(LinearLayout.HORIZONTAL);
        statusRow.setGravity(Gravity.CENTER_VERTICAL);
        statusRow.setPadding(0, dp(10), 0, 0);

        statusChip = text("");
        statusChip.setTextSize(13);
        statusChip.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        statusChip.setIncludeFontPadding(false);
        statusChip.setPadding(dp(10), dp(6), dp(10), dp(6));
        statusRow.addView(statusChip);

        header.addView(statusRow);
        return header;
    }

    private View navigation() {
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);

        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setPadding(0, dp(2), 0, dp(16));

        loginTab = tabButton("Account", () -> showScreen(loginScreen));
        weatherTab = tabButton("Weather", () -> showScreen(weatherScreen));
        recommendationTab = tabButton("Recommend", () -> showScreen(recommendationScreen));
        chatTab = tabButton("Chat", () -> showScreen(chatScreen));
        historyTab = tabButton("History", () -> showScreen(historyScreen));

        navigation.addView(loginTab);
        navigation.addView(weatherTab);
        navigation.addView(recommendationTab);
        navigation.addView(chatTab);
        navigation.addView(historyTab);

        scrollView.addView(navigation);
        return scrollView;
    }

    private void buildLoginScreen() {
        emailInput = input("name@example.com", false);
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordInput = input("Password", true);
        authOutput = output("Auth status will appear here.");

        loginScreen.addView(field("Email", emailInput));
        loginScreen.addView(field("Password", passwordInput));

        LinearLayout actions = horizontalActions();
        registerButton = weightedButton(primaryButton("Register", this::register));
        loginButton = weightedButton(secondaryButton("Login", this::login));
        actions.addView(registerButton);
        actions.addView(loginButton);
        loginScreen.addView(actions);

        signOutButton = secondaryButton("Sign out", this::clearToken);
        loginScreen.addView(signOutButton);
        loginScreen.addView(authOutput);
    }

    private void buildWeatherScreen() {
        weatherCityInput = input("Vilnius", false);
        weatherOutput = output("Weather result will appear here.");

        weatherScreen.addView(field("City", weatherCityInput));
        weatherButton = primaryButton("Get weather", this::getWeather);
        weatherScreen.addView(weatherButton);
        weatherScreen.addView(weatherOutput);
    }

    private void buildRecommendationScreen() {
        recommendationCityInput = input("Vilnius", false);
        occasionInput = input("walk, work, date, travel", false);
        recommendationOutput = output("Recommendation will appear here.");

        recommendationScreen.addView(field("City", recommendationCityInput));
        recommendationScreen.addView(field("Occasion", occasionInput));
        recommendationButton = primaryButton("Get recommendation", this::getRecommendation);
        recommendationScreen.addView(recommendationButton);
        recommendationScreen.addView(recommendationOutput);
    }

    private void buildChatScreen() {
        chatMessageInput = input("Ask about style or weather", false);
        chatMessageInput.setSingleLine(false);
        chatMessageInput.setMinLines(3);
        chatMessageInput.setGravity(Gravity.TOP | Gravity.START);
        chatOutput = output("Assistant answer will appear here.");

        chatScreen.addView(field("Message", chatMessageInput));
        chatSendButton = primaryButton("Send", this::sendChatMessage);
        chatScreen.addView(chatSendButton);
        chatScreen.addView(secondaryButton("New chat", () -> {
            chatSessionId = null;
            chatOutput.setText("New chat started.");
        }));
        chatScreen.addView(chatOutput);
    }

    private void buildHistoryScreen() {
        historyOutput = output("History will appear here.");

        LinearLayout actions = horizontalActions();
        historyLoadButton = weightedButton(primaryButton("Load history", this::loadHistory));
        historyClearButton = weightedButton(secondaryButton("Clear history", this::clearHistory));
        actions.addView(historyLoadButton);
        actions.addView(historyClearButton);
        historyScreen.addView(actions);
        historyScreen.addView(historyOutput);
    }

    private void register() {
        postAuth("/auth/register", registerButton);
    }

    private void login() {
        postAuth("/auth/login", loginButton);
    }

    private void postAuth(String path, Button sourceButton) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            authOutput.setText("Enter email and password.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "email", email);
        put(body, "password", password);

        setLoading(sourceButton, true);
        authOutput.setText("Connecting...");

        api("POST", path, body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(sourceButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    token = json.optString("token", "");
                    if (token.trim().isEmpty()) {
                        authOutput.setText("Authentication response does not contain JWT.");
                        return;
                    }

                    preferences.edit().putString(KEY_TOKEN, token).apply();
                    updateStatus();
                    authOutput.setText("Signed in as " + json.optString("email"));
                    showScreen(weatherScreen);
                } catch (Exception ex) {
                    authOutput.setText("Authentication response error: " + ex.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                setLoading(sourceButton, false);
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
            weatherOutput.setText("Enter a city.");
            return;
        }

        setLoading(weatherButton, true);
        weatherOutput.setText("Loading weather...");

        api("GET", "/weather?city=" + encode(city), null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(weatherButton, false);
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
                setLoading(weatherButton, false);
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
            recommendationOutput.setText("Enter a city.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "city", city);
        if (!occasion.isEmpty()) {
            put(body, "occasion", occasion);
        }

        setLoading(recommendationButton, true);
        recommendationOutput.setText("Getting short recommendation...");

        api("POST", "/recommendations", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(recommendationButton, false);
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
                setLoading(recommendationButton, false);
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
            chatOutput.setText("Enter a message.");
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "message", message);
        if (chatSessionId != null) {
            put(body, "sessionId", chatSessionId);
        }

        setLoading(chatSendButton, true);
        chatOutput.setText("Sending...");

        api("POST", "/chat", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(chatSendButton, false);
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
                setLoading(chatSendButton, false);
                chatOutput.setText(message);
            }
        });
    }

    private void loadHistory() {
        if (!requireToken(historyOutput)) {
            return;
        }

        setLoading(historyLoadButton, true);
        historyOutput.setText("Loading history...");

        api("GET", "/history", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(historyLoadButton, false);
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
                setLoading(historyLoadButton, false);
                historyOutput.setText(message);
            }
        });
    }

    private void clearHistory() {
        if (!requireToken(historyOutput)) {
            return;
        }

        setLoading(historyClearButton, true);
        historyOutput.setText("Clearing history...");

        api("DELETE", "/history", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(historyClearButton, false);
                historyOutput.setText("History cleared.");
            }

            @Override
            public void onError(String message) {
                setLoading(historyClearButton, false);
                historyOutput.setText(message);
            }
        });
    }

    private void clearToken() {
        token = "";
        preferences.edit().remove(KEY_TOKEN).apply();
        updateStatus();
        authOutput.setText("Token cleared.");
        showScreen(loginScreen);
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
        String baseUrl = preferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
        }

        preferences.edit()
                .putString(KEY_BASE_URL, baseUrl.trim())
                .apply();

        return baseUrl.trim();
    }

    private void updateStatus() {
        boolean signedIn = hasToken();
        statusChip.setText(signedIn ? "Signed in" : "Not signed in");
        statusChip.setTextColor(signedIn ? COLOR_PRIMARY_DARK : COLOR_DANGER);
        statusChip.setBackground(chipBackground(
                signedIn ? Color.rgb(238, 240, 242) : Color.rgb(250, 241, 241),
                signedIn ? Color.rgb(222, 226, 231) : Color.rgb(235, 211, 211)
        ));
        if (signOutButton != null) {
            signOutButton.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
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

        styleTab(loginTab, visibleScreen == loginScreen);
        styleTab(weatherTab, visibleScreen == weatherScreen);
        styleTab(recommendationTab, visibleScreen == recommendationScreen);
        styleTab(chatTab, visibleScreen == chatScreen);
        styleTab(historyTab, visibleScreen == historyScreen);
    }

    private LinearLayout screen(String title) {
        LinearLayout layout = verticalLayout();
        layout.setPadding(dp(16), dp(17), dp(16), dp(18));
        layout.setBackground(panelBackground(COLOR_SURFACE, COLOR_BORDER));
        layout.setElevation(dp(2));

        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(16));
        layout.setLayoutParams(params);

        TextView titleView = text(title);
        titleView.setTextSize(20);
        titleView.setTextColor(COLOR_TEXT);
        titleView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        titleView.setPadding(0, 0, 0, dp(12));
        layout.addView(titleView);
        return layout;
    }

    private LinearLayout field(String label, EditText input) {
        LinearLayout layout = verticalLayout();
        layout.addView(label(label));
        layout.addView(input);
        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(12));
        layout.setLayoutParams(params);
        return layout;
    }

    private LinearLayout horizontalActions() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(matchWrap());
        return layout;
    }

    private LinearLayout verticalLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private TextView text(String value) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(15);
        view.setTextColor(COLOR_TEXT);
        view.setPadding(0, dp(4), 0, dp(4));
        return view;
    }

    private TextView label(String value) {
        TextView view = text(value);
        view.setTextSize(13);
        view.setTextColor(COLOR_MUTED);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setPadding(0, 0, 0, dp(6));
        return view;
    }

    private TextView output(String emptyText) {
        TextView view = text(emptyText);
        view.setTextIsSelectable(true);
        view.setTextColor(COLOR_MUTED);
        view.setTextSize(14);
        view.setMinHeight(dp(88));
        view.setPadding(dp(12), dp(11), dp(12), dp(11));
        view.setBackground(panelBackground(COLOR_OUTPUT_SURFACE, COLOR_SOFT_BORDER));

        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, dp(12), 0, 0);
        view.setLayoutParams(params);
        return view;
    }

    private EditText input(String hint, boolean password) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setTextSize(15);
        editText.setTextColor(COLOR_TEXT);
        editText.setHintTextColor(Color.rgb(138, 148, 158));
        editText.setSingleLine(true);
        editText.setInputType(password
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                : InputType.TYPE_CLASS_TEXT);
        editText.setMinHeight(dp(48));
        editText.setElevation(dp(0));
        editText.setBackground(panelBackground(COLOR_FIELD, COLOR_BORDER));
        editText.setPadding(dp(12), 0, dp(12), 0);
        editText.setSelectAllOnFocus(false);
        return editText;
    }

    private Button tabButton(String text, Runnable action) {
        Button button = buttonBase(text, action);
        button.setTextSize(13);
        button.setMinHeight(dp(38));
        button.setMinimumHeight(dp(38));
        button.setPadding(dp(12), 0, dp(12), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dp(38)
        );
        params.setMargins(0, 0, dp(7), 0);
        button.setLayoutParams(params);
        return button;
    }

    private Button primaryButton(String text, Runnable action) {
        Button button = buttonBase(text, action);
        button.setTextColor(Color.WHITE);
        button.setBackground(buttonBackground(COLOR_PRIMARY, COLOR_PRIMARY));
        return withFullWidthMargins(button);
    }

    private Button secondaryButton(String text, Runnable action) {
        Button button = buttonBase(text, action);
        button.setTextColor(COLOR_PRIMARY_DARK);
        button.setBackground(buttonBackground(COLOR_SURFACE, COLOR_BORDER));
        return withFullWidthMargins(button);
    }

    private Button buttonBase(String text, Runnable action) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(dp(46));
        button.setMinimumHeight(dp(46));
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setOnClickListener(view -> action.run());
        return button;
    }

    private Button withFullWidthMargins(Button button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(46)
        );
        params.setMargins(0, 0, 0, dp(10));
        button.setLayoutParams(params);
        return button;
    }

    private Button weightedButton(Button button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                dp(46),
                1f
        );
        params.setMargins(0, 0, dp(8), dp(10));
        button.setLayoutParams(params);
        return button;
    }

    private void styleTab(Button button, boolean selected) {
        if (button == null) {
            return;
        }

        button.setTextColor(selected ? Color.WHITE : COLOR_PRIMARY_DARK);
        button.setBackground(selected
                ? buttonBackground(COLOR_PRIMARY, COLOR_PRIMARY)
                : buttonBackground(COLOR_SURFACE, COLOR_BORDER));
    }

    private void applySystemBars() {
        getWindow().setStatusBarColor(COLOR_BACKGROUND);
        getWindow().setNavigationBarColor(COLOR_SURFACE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );
    }

    private void setLoading(Button button, boolean loading) {
        if (button == null) {
            return;
        }

        button.setEnabled(!loading);
        button.setAlpha(loading ? 0.55f : 1f);
    }

    private GradientDrawable panelBackground(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(10));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private GradientDrawable buttonBackground(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(10));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private GradientDrawable chipBackground(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
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
