package com.weatherwear.mobile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String PREFS = "weatherwear";
    private static final String KEY_TOKEN = "jwt";
    private static final String KEY_BASE_URL = "baseUrl";
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8090/api";
    private static final int LOCATION_PERMISSION_REQUEST = 701;
    private static final int LOCATION_TARGET_NONE = 0;
    private static final int LOCATION_TARGET_WEATHER = 1;
    private static final int LOCATION_TARGET_RECOMMENDATION = 2;

    private static final String[] STYLE_VALUES = {
            "CASUAL", "BUSINESS", "SPORTY", "STREETWEAR", "ELEGANT", "MINIMALIST"
    };
    private static final String[] SENSITIVITY_VALUES = {"LOW", "MEDIUM", "HIGH"};
    private static final String[] ACTIVITY_VALUES = {"LOW", "MEDIUM", "HIGH"};
    private static final String[] FEEDBACK_VALUES = {"RATING", "LIKE", "DISLIKE", "COMMENT"};

    private int COLOR_BACKGROUND;
    private int COLOR_SURFACE;
    private int COLOR_OUTPUT_SURFACE;
    private int COLOR_PRIMARY;
    private int COLOR_PRIMARY_DARK;
    private int COLOR_TEXT;
    private int COLOR_MUTED;
    private int COLOR_HINT;
    private int COLOR_BORDER;
    private int COLOR_SOFT_BORDER;
    private int COLOR_FIELD;
    private int COLOR_DANGER;
    private int COLOR_SUCCESS;
    private int COLOR_BLUE_FILL;
    private int COLOR_BLUE_BORDER;
    private int COLOR_GREEN_FILL;
    private int COLOR_GREEN_BORDER;
    private int COLOR_WARM_FILL;
    private int COLOR_WARM_BORDER;
    private int COLOR_STATUS_SIGNED_FILL;
    private int COLOR_STATUS_SIGNED_BORDER;
    private int COLOR_STATUS_SIGNED_OUT_FILL;
    private int COLOR_STATUS_SIGNED_OUT_BORDER;

    private ApiClient apiClient;
    private SharedPreferences preferences;
    private String token;
    private String lastWeatherCity = "";
    private Long chatSessionId;
    private int loadingOperations;
    private int pendingLocationTarget = LOCATION_TARGET_NONE;

    private TextView statusChip;
    private ProgressBar loadingSpinner;
    private LinearLayout bottomNavigation;
    private TextView profileToolsLabel;

    private EditText baseUrlInput;
    private EditText emailInput;
    private EditText passwordInput;

    private EditText weatherCityInput;
    private EditText weatherLatInput;
    private EditText weatherLonInput;

    private EditText recommendationCityInput;
    private EditText recommendationLatInput;
    private EditText recommendationLonInput;
    private EditText occasionInput;

    private Spinner stylePreferenceInput;
    private Spinner coldSensitivityInput;
    private Spinner heatSensitivityInput;
    private Spinner windSensitivityInput;
    private Spinner rainSensitivityInput;
    private EditText maxLayersInput;
    private CheckBox prefersHeadwearInput;
    private CheckBox prefersWaterproofInput;
    private Spinner activityLevelInput;
    private EditText preferredColorsInput;
    private LinearLayout preferredColorsPreview;
    private EditText avoidItemsInput;

    private EditText chatCityInput;
    private EditText chatSessionInput;
    private EditText chatMessageInput;

    private EditText historyItemIdInput;

    private EditText feedbackHistoryIdInput;
    private Spinner feedbackTypeInput;
    private EditText feedbackCommentInput;
    private EditText feedbackDeleteIdInput;
    private Button[] feedbackRatingButtons;
    private int selectedFeedbackRating = 5;

    private TextView authOutput;
    private TextView weatherOutput;
    private TextView recommendationOutput;
    private TextView preferencesOutput;
    private TextView chatOutput;
    private TextView historyOutput;
    private TextView feedbackOutput;

    private LinearLayout chatSessionPanel;
    private TextView chatSessionTitle;
    private TextView chatSessionMeta;
    private TextView chatActivityLabel;
    private LinearLayout chatListPanel;
    private boolean chatShowingMessages = true;
    private TextView historyListLabel;
    private LinearLayout historyListPanel;
    private LinearLayout historyDetailPanel;
    private TextView historyDetailTitle;
    private TextView historyDetailDate;
    private TextView historyDetailWeather;
    private TextView historyDetailRecommendation;
    private LinearLayout feedbackListPanel;
    private Long selectedHistoryId;
    private String selectedHistoryCity = "";
    private String selectedHistoryCreatedAt = "";
    private String selectedHistoryRecommendation = "";

    private LinearLayout feedbackContextPanel;
    private TextView feedbackContextTitle;
    private TextView feedbackContextSubtitle;
    private TextView feedbackContextPreview;

    private LinearLayout weatherCardPanel;
    private TextView weatherCardIcon;
    private TextView weatherCardCity;
    private TextView weatherCardTemperature;
    private TextView weatherCardCondition;
    private TextView weatherCardFeels;
    private TextView weatherCardWind;
    private TextView weatherCardHumidity;
    private TextView weatherCardPrecipitation;
    private TextView weatherCardCached;

    private LinearLayout recommendationCardPanel;
    private TextView recommendationCardCity;
    private TextView recommendationCardWeather;
    private TextView recommendationCardText;

    private LinearLayout loginScreen;
    private LinearLayout weatherScreen;
    private LinearLayout recommendationScreen;
    private LinearLayout preferencesScreen;
    private LinearLayout chatScreen;
    private LinearLayout historyScreen;
    private LinearLayout feedbackScreen;

    private Button loginTab;
    private Button weatherTab;
    private Button recommendationTab;
    private Button preferencesTab;
    private Button chatTab;
    private Button historyTab;
    private Button feedbackTab;

    private Button registerButton;
    private Button loginButton;
    private Button saveBaseUrlButton;
    private Button healthButton;
    private Button signOutButton;
    private Button deleteAccountButton;
    private Button preferencesShortcutButton;
    private Button feedbackShortcutButton;

    private Button weatherButton;
    private Button weatherCoordinatesButton;
    private Button weatherLocationButton;
    private Button weatherRecommendButton;
    private Button recommendationButton;
    private Button recommendationCoordinatesButton;
    private Button recommendationLocationButton;
    private Button recommendationHistoryButton;
    private Button preferencesLoadButton;
    private Button preferencesSaveButton;
    private Button chatSendButton;
    private Button chatLoadSessionsButton;
    private Button chatLoadMessagesButton;
    private Button chatDeleteSessionButton;
    private Button historyLoadButton;
    private Button historyDetailButton;
    private Button historyClearButton;
    private Button historyBackButton;
    private Button historyFeedbackButton;
    private Button feedbackSubmitButton;
    private Button feedbackLoadButton;
    private Button feedbackLoadForRecommendationButton;
    private Button feedbackDeleteButton;
    private Button feedbackRatingTypeButton;
    private Button feedbackLikeTypeButton;
    private Button feedbackDislikeTypeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        token = preferences.getString(KEY_TOKEN, "");
        apiClient = new ApiClient(new Handler(Looper.getMainLooper()));

        loadDesignResources();
        applySystemBars();
        buildUi();
        updateStatus();
        showScreen(hasToken() ? weatherScreen : loginScreen);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST) {
            return;
        }

        int target = pendingLocationTarget;
        if (target == LOCATION_TARGET_NONE) {
            return;
        }

        if (isLocationPermissionGranted(grantResults)) {
            loadCurrentLocation(target);
        } else {
            finishLocationRequest(target);
            showError(locationOutputForTarget(target), s(R.string.location_permission_denied));
        }
    }

    private void buildUi() {
        FrameLayout appFrame = new FrameLayout(this);
        appFrame.setBackgroundColor(COLOR_BACKGROUND);

        LinearLayout shell = verticalLayout();
        shell.setBackgroundColor(COLOR_BACKGROUND);
        appFrame.addView(shell, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(COLOR_BACKGROUND);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout root = verticalLayout();
        root.setPadding(
                dimen(R.dimen.screen_padding_horizontal),
                dimen(R.dimen.screen_padding_top),
                dimen(R.dimen.screen_padding_horizontal),
                dimen(R.dimen.screen_padding_bottom)
        );
        scrollView.addView(root);

        root.addView(appHeader());

        loginScreen = screen(s(R.string.screen_profile));
        weatherScreen = screen(s(R.string.screen_weather));
        recommendationScreen = screen(s(R.string.screen_recommendation));
        preferencesScreen = screen(s(R.string.screen_preferences));
        chatScreen = screen(s(R.string.screen_chat));
        historyScreen = screen(s(R.string.screen_history));
        feedbackScreen = screen(s(R.string.screen_feedback));

        buildLoginScreen();
        buildWeatherScreen();
        buildRecommendationScreen();
        buildPreferencesScreen();
        buildChatScreen();
        buildHistoryScreen();
        buildFeedbackScreen();

        root.addView(loginScreen);
        root.addView(weatherScreen);
        root.addView(recommendationScreen);
        root.addView(preferencesScreen);
        root.addView(chatScreen);
        root.addView(historyScreen);
        root.addView(feedbackScreen);

        shell.addView(scrollView);
        bottomNavigation = bottomNavigation();
        shell.addView(bottomNavigation);

        loadingSpinner = new ProgressBar(this);
        loadingSpinner.setVisibility(View.GONE);
        FrameLayout.LayoutParams spinnerParams = new FrameLayout.LayoutParams(
                dimen(R.dimen.spinner_size),
                dimen(R.dimen.spinner_size),
                Gravity.CENTER
        );
        appFrame.addView(loadingSpinner, spinnerParams);

        setContentView(appFrame);
    }

    private View appHeader() {
        LinearLayout header = verticalLayout();
        header.setPadding(0, dp(2), 0, dp(8));

        TextView title = text(s(R.string.app_name));
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

    private LinearLayout bottomNavigation() {
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setGravity(Gravity.CENTER);
        navigation.setPadding(dp(6), dp(6), dp(6), dp(6));
        navigation.setBackground(panelBackground(COLOR_SURFACE, COLOR_BORDER));
        navigation.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dimen(R.dimen.bottom_nav_height)
        ));

        weatherTab = navButton(s(R.string.nav_weather), () -> showScreen(weatherScreen));
        recommendationTab = navButton(s(R.string.nav_recommend), () -> showScreen(recommendationScreen));
        chatTab = navButton(s(R.string.nav_chat), () -> showScreen(chatScreen));
        historyTab = navButton(s(R.string.nav_history), () -> showScreen(historyScreen));
        loginTab = navButton(s(R.string.nav_profile), () -> showScreen(loginScreen));

        navigation.addView(weatherTab);
        navigation.addView(recommendationTab);
        navigation.addView(chatTab);
        navigation.addView(historyTab);
        navigation.addView(loginTab);

        return navigation;
    }

    private void buildLoginScreen() {
        baseUrlInput = input(DEFAULT_BASE_URL, false);
        baseUrlInput.setText(currentBaseUrl());
        emailInput = input(s(R.string.hint_email), false);
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordInput = input(s(R.string.hint_password), true);
        authOutput = output(s(R.string.output_auth_empty));

        loginScreen.addView(field(s(R.string.label_backend_url), baseUrlInput));
        LinearLayout configActions = horizontalActions();
        saveBaseUrlButton = weightedButton(secondaryButton(s(R.string.button_save_url), this::saveBaseUrl));
        healthButton = weightedButton(secondaryButton(s(R.string.button_health), this::checkHealth));
        configActions.addView(saveBaseUrlButton);
        configActions.addView(healthButton);
        loginScreen.addView(configActions);

        loginScreen.addView(field(s(R.string.label_email), emailInput));
        loginScreen.addView(field(s(R.string.label_password), passwordInput));

        LinearLayout actions = horizontalActions();
        registerButton = weightedButton(primaryButton(s(R.string.button_register), this::register));
        loginButton = weightedButton(secondaryButton(s(R.string.button_login), this::login));
        actions.addView(registerButton);
        actions.addView(loginButton);
        loginScreen.addView(actions);

        signOutButton = secondaryButton(s(R.string.button_sign_out), this::clearToken);
        deleteAccountButton = dangerButton(s(R.string.button_delete_account), this::confirmDeleteAccount);
        profileToolsLabel = sectionLabel(s(R.string.label_profile_tools));
        loginScreen.addView(profileToolsLabel);
        LinearLayout profileActions = horizontalActions();
        preferencesShortcutButton = weightedButton(
                secondaryButton(s(R.string.button_preferences), () -> showScreen(preferencesScreen))
        );
        feedbackShortcutButton = weightedButton(
                secondaryButton(s(R.string.button_feedback), () -> showScreen(feedbackScreen))
        );
        profileActions.addView(preferencesShortcutButton);
        profileActions.addView(feedbackShortcutButton);
        loginScreen.addView(profileActions);
        loginScreen.addView(signOutButton);
        loginScreen.addView(deleteAccountButton);
        loginScreen.addView(authOutput);
    }

    private void buildWeatherScreen() {
        weatherCityInput = input(s(R.string.hint_city), false);
        weatherLatInput = decimalInput("54.6872");
        weatherLonInput = decimalInput("25.2797");
        weatherOutput = output(s(R.string.output_weather_empty));

        weatherScreen.addView(field(s(R.string.label_city), weatherCityInput));
        weatherButton = primaryButton(s(R.string.button_get_weather), this::getWeatherByCity);
        weatherScreen.addView(weatherButton);

        weatherScreen.addView(field(s(R.string.label_latitude), weatherLatInput));
        weatherScreen.addView(field(s(R.string.label_longitude), weatherLonInput));
        weatherCoordinatesButton = secondaryButton(
                s(R.string.button_get_by_coordinates),
                this::getWeatherByCoordinates
        );
        weatherScreen.addView(weatherCoordinatesButton);
        weatherLocationButton = secondaryButton(s(R.string.button_use_my_location), this::useLocationForWeather);
        weatherScreen.addView(weatherLocationButton);
        weatherScreen.addView(sectionLabel(s(R.string.label_current_conditions)));
        weatherScreen.addView(weatherVisualPanel());
        weatherRecommendButton = primaryButton(
                s(R.string.button_recommend_from_weather),
                this::prepareRecommendationFromWeather
        );
        weatherScreen.addView(weatherRecommendButton);
        weatherScreen.addView(weatherOutput);
    }

    private void buildRecommendationScreen() {
        recommendationCityInput = input(s(R.string.hint_city), false);
        recommendationLatInput = decimalInput("54.6872");
        recommendationLonInput = decimalInput("25.2797");
        occasionInput = input(s(R.string.hint_occasion), false);
        recommendationOutput = output(s(R.string.output_recommendation_empty));

        recommendationScreen.addView(field(s(R.string.label_city), recommendationCityInput));
        recommendationScreen.addView(field(s(R.string.label_occasion), occasionInput));
        recommendationButton = primaryButton(
                s(R.string.button_get_recommendation),
                this::getRecommendationByCity
        );
        recommendationScreen.addView(recommendationButton);

        recommendationScreen.addView(field(s(R.string.label_latitude), recommendationLatInput));
        recommendationScreen.addView(field(s(R.string.label_longitude), recommendationLonInput));
        recommendationCoordinatesButton = secondaryButton(
                s(R.string.button_use_coordinates),
                this::getRecommendationByCoordinates
        );
        recommendationScreen.addView(recommendationCoordinatesButton);
        recommendationLocationButton = secondaryButton(
                s(R.string.button_use_my_location),
                this::useLocationForRecommendation
        );
        recommendationScreen.addView(recommendationLocationButton);
        recommendationScreen.addView(sectionLabel(s(R.string.label_outfit_card)));
        recommendationScreen.addView(recommendationVisualPanel());
        recommendationHistoryButton = secondaryButton(s(R.string.button_view_history), this::viewRecommendationHistory);
        recommendationScreen.addView(recommendationHistoryButton);
        recommendationScreen.addView(recommendationOutput);
    }

    private void buildPreferencesScreen() {
        stylePreferenceInput = spinner(STYLE_VALUES);
        coldSensitivityInput = spinner(SENSITIVITY_VALUES);
        heatSensitivityInput = spinner(SENSITIVITY_VALUES);
        windSensitivityInput = spinner(SENSITIVITY_VALUES);
        rainSensitivityInput = spinner(SENSITIVITY_VALUES);
        maxLayersInput = numberInput("3");
        prefersHeadwearInput = checkbox(s(R.string.label_prefers_headwear));
        prefersWaterproofInput = checkbox(s(R.string.label_prefers_waterproof));
        activityLevelInput = spinner(ACTIVITY_VALUES);
        preferredColorsInput = input(s(R.string.hint_preferred_colors), false);
        avoidItemsInput = input(s(R.string.hint_avoid_items), false);
        preferencesOutput = output(s(R.string.output_preferences_empty));
        preferredColorsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence value, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence value, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable value) {
                updateColorPreview();
            }
        });

        setPreferenceDefaults();

        LinearLayout actions = horizontalActions();
        preferencesLoadButton = weightedButton(primaryButton(s(R.string.button_load), this::loadPreferences));
        preferencesSaveButton = weightedButton(secondaryButton(s(R.string.button_save), this::savePreferences));
        actions.addView(preferencesLoadButton);
        actions.addView(preferencesSaveButton);
        preferencesScreen.addView(actions);

        preferencesScreen.addView(preferenceStyleCard());
        preferencesScreen.addView(preferenceSensitivityCard());
        preferencesScreen.addView(preferenceComfortCard());
        preferencesScreen.addView(preferencesOutput);
        updateColorPreview();
    }

    private void buildChatScreen() {
        chatCityInput = input(s(R.string.hint_optional_city), false);
        chatSessionInput = numberInput(s(R.string.hint_session_id));
        chatMessageInput = input(s(R.string.hint_chat_message), false);
        chatMessageInput.setSingleLine(false);
        chatMessageInput.setMinLines(3);
        chatMessageInput.setGravity(Gravity.TOP | Gravity.START);
        chatOutput = output(s(R.string.output_chat_empty));
        chatListPanel = listPanel(s(R.string.chat_empty));

        chatSessionPanel = chatSessionView();
        chatScreen.addView(chatSessionPanel);
        chatScreen.addView(field(s(R.string.label_city_context), chatCityInput));
        chatScreen.addView(field(s(R.string.label_session_id), chatSessionInput));
        chatScreen.addView(field(s(R.string.label_message), chatMessageInput));

        LinearLayout sendActions = horizontalActions();
        chatSendButton = weightedButton(primaryButton(s(R.string.button_send), this::sendChatMessage));
        sendActions.addView(chatSendButton);
        sendActions.addView(weightedButton(secondaryButton(s(R.string.button_new_chat), this::newChat)));
        chatScreen.addView(sendActions);

        LinearLayout sessionActions = horizontalActions();
        chatLoadSessionsButton = weightedButton(secondaryButton(s(R.string.button_sessions), this::loadChatSessions));
        chatLoadMessagesButton = weightedButton(secondaryButton(s(R.string.button_messages), this::loadChatMessages));
        sessionActions.addView(chatLoadSessionsButton);
        sessionActions.addView(chatLoadMessagesButton);
        chatScreen.addView(sessionActions);

        chatDeleteSessionButton = dangerButton(s(R.string.button_delete_session), this::confirmDeleteChatSession);
        chatScreen.addView(chatDeleteSessionButton);
        chatActivityLabel = sectionLabel(s(R.string.chat_conversation));
        chatScreen.addView(chatActivityLabel);
        chatScreen.addView(chatListPanel);
        chatScreen.addView(chatOutput);
        updateChatSessionView();
    }

    private void buildHistoryScreen() {
        historyItemIdInput = numberInput(s(R.string.hint_history_id));
        historyOutput = output(s(R.string.output_history_empty));
        historyListPanel = listPanel(s(R.string.empty_history_not_loaded));

        LinearLayout actions = horizontalActions();
        historyLoadButton = weightedButton(primaryButton(s(R.string.button_load_history), this::loadHistory));
        historyClearButton = weightedButton(secondaryButton(s(R.string.button_clear_history), this::confirmClearHistory));
        actions.addView(historyLoadButton);
        actions.addView(historyClearButton);
        historyScreen.addView(actions);

        historyScreen.addView(field(s(R.string.label_history_item_id), historyItemIdInput));
        historyDetailButton = secondaryButton(s(R.string.button_load_item), this::loadHistoryDetail);
        historyScreen.addView(historyDetailButton);
        historyListLabel = sectionLabel(s(R.string.label_saved_recommendations));
        historyScreen.addView(historyListLabel);
        historyScreen.addView(historyListPanel);
        historyDetailPanel = historyDetailView();
        historyScreen.addView(historyDetailPanel);
        historyScreen.addView(historyOutput);
    }

    private void buildFeedbackScreen() {
        feedbackHistoryIdInput = numberInput(s(R.string.hint_recommendation_history_id));
        feedbackTypeInput = spinner(FEEDBACK_VALUES);
        feedbackCommentInput = input(s(R.string.hint_feedback_comment), false);
        feedbackDeleteIdInput = numberInput(s(R.string.hint_feedback_id));
        feedbackOutput = output(s(R.string.output_feedback_empty));
        feedbackListPanel = listPanel(s(R.string.empty_feedback_not_loaded));

        feedbackContextPanel = feedbackContextView();
        feedbackScreen.addView(feedbackContextPanel);
        feedbackScreen.addView(field(s(R.string.label_recommendation_history_id), feedbackHistoryIdInput));
        feedbackScreen.addView(feedbackTypeSelector());
        feedbackScreen.addView(ratingSelector());
        feedbackScreen.addView(field(s(R.string.label_comment), feedbackCommentInput));

        feedbackSubmitButton = primaryButton(s(R.string.button_submit_feedback), this::submitFeedback);
        feedbackScreen.addView(feedbackSubmitButton);

        LinearLayout loadActions = horizontalActions();
        feedbackLoadButton = weightedButton(secondaryButton(s(R.string.button_load_all), this::loadFeedback));
        feedbackLoadForRecommendationButton = weightedButton(
                secondaryButton(s(R.string.button_load_for_item), this::loadFeedbackForRecommendation)
        );
        loadActions.addView(feedbackLoadButton);
        loadActions.addView(feedbackLoadForRecommendationButton);
        feedbackScreen.addView(loadActions);

        feedbackScreen.addView(field(s(R.string.label_feedback_id), feedbackDeleteIdInput));
        feedbackDeleteButton = dangerButton(s(R.string.button_delete_feedback), this::confirmDeleteFeedback);
        feedbackScreen.addView(feedbackDeleteButton);
        feedbackScreen.addView(sectionLabel(s(R.string.label_feedback_entries)));
        feedbackScreen.addView(feedbackListPanel);
        feedbackScreen.addView(feedbackOutput);
    }

    private void setPreferenceDefaults() {
        selectSpinnerValue(stylePreferenceInput, "CASUAL");
        selectSpinnerValue(coldSensitivityInput, "MEDIUM");
        selectSpinnerValue(heatSensitivityInput, "MEDIUM");
        selectSpinnerValue(windSensitivityInput, "MEDIUM");
        selectSpinnerValue(rainSensitivityInput, "MEDIUM");
        maxLayersInput.setText("3");
        selectSpinnerValue(activityLevelInput, "MEDIUM");
        preferredColorsInput.setText("");
        avoidItemsInput.setText("");
    }

    private void register() {
        postAuth("/auth/register", registerButton);
    }

    private void login() {
        postAuth("/auth/login", loginButton);
    }

    private void postAuth(String path, Button sourceButton) {
        String email = value(emailInput);
        String password = passwordInput.getText().toString();

        clearFieldError(emailInput);
        clearFieldError(passwordInput);
        if (email.isEmpty()) {
            showFieldError(emailInput, authOutput, s(R.string.error_enter_email));
            return;
        }
        if (password.isEmpty()) {
            showFieldError(passwordInput, authOutput, s(R.string.error_enter_password));
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "email", email);
        put(body, "password", password);

        setLoading(sourceButton, true);
        authOutput.setText(s(R.string.status_connecting));

        api("POST", path, body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(sourceButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    token = json.optString("token", "");
                    if (token.trim().isEmpty()) {
                        authOutput.setText(s(R.string.error_missing_jwt));
                        return;
                    }

                    preferences.edit().putString(KEY_TOKEN, token).apply();
                    updateStatus();
                    authOutput.setText(s(R.string.auth_signed_in_as, json.optString("email")));
                    showScreen(weatherScreen);
                } catch (Exception ex) {
                    authOutput.setText(s(R.string.error_auth_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(sourceButton, false);
                showError(authOutput, message);
            }
        });
    }

    private void saveBaseUrl() {
        String baseUrl = normalizeBaseUrl(value(baseUrlInput));
        preferences.edit().putString(KEY_BASE_URL, baseUrl).apply();
        baseUrlInput.setText(baseUrl);
        authOutput.setText(s(R.string.status_backend_url_saved, baseUrl));
    }

    private void checkHealth() {
        saveBaseUrl();
        setLoading(healthButton, true);
        authOutput.setText(s(R.string.status_checking_service));

        api("GET", "/health", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(healthButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    authOutput.setText(s(
                            R.string.status_service,
                            json.optString("status", "UP"),
                            json.optString("service"),
                            json.optString("timestamp")
                    ));
                } catch (Exception ex) {
                    authOutput.setText(response);
                }
            }

            @Override
            public void onError(String message) {
                setLoading(healthButton, false);
                showError(authOutput, message);
            }
        });
    }

    private void confirmDeleteAccount() {
        if (!requireToken(authOutput)) {
            return;
        }

        confirm(
                s(R.string.confirm_delete_account_title),
                s(R.string.confirm_delete_account_message),
                this::deleteAccount
        );
    }

    private void deleteAccount() {
        setLoading(deleteAccountButton, true);
        authOutput.setText(s(R.string.status_delete_account));

        api("DELETE", "/users/me", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(deleteAccountButton, false);
                token = "";
                chatSessionId = null;
                preferences.edit().remove(KEY_TOKEN).apply();
                updateStatus();
                authOutput.setText(s(R.string.status_account_deleted));
                showScreen(loginScreen);
            }

            @Override
            public void onError(String message) {
                setLoading(deleteAccountButton, false);
                showError(authOutput, message);
            }
        });
    }

    private void getWeatherByCity() {
        if (!requireToken(weatherOutput)) {
            return;
        }

        String city = value(weatherCityInput);
        clearFieldError(weatherCityInput);
        if (city.isEmpty()) {
            showFieldError(weatherCityInput, weatherOutput, s(R.string.error_enter_city));
            return;
        }

        setLoading(weatherButton, true);
        weatherOutput.setText(s(R.string.status_loading_weather));

        api("GET", "/weather?city=" + encode(city), null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(weatherButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    lastWeatherCity = displayCity(json.optString("city", city));
                    fillCityFields(lastWeatherCity);
                    renderWeatherCard(json, city);
                    weatherOutput.setText(weatherStatus(json, city));
                } catch (Exception ex) {
                    weatherOutput.setText(s(R.string.error_weather_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(weatherButton, false);
                showError(weatherOutput, message);
            }
        });
    }

    private void getWeatherByCoordinates() {
        if (!requireToken(weatherOutput)) {
            return;
        }

        Double lat = parseCoordinate(weatherLatInput, s(R.string.label_latitude), -90, 90, weatherOutput);
        if (lat == null) {
            return;
        }
        Double lon = parseCoordinate(weatherLonInput, s(R.string.label_longitude), -180, 180, weatherOutput);
        if (lon == null) {
            return;
        }

        setLoading(weatherCoordinatesButton, true);
        weatherOutput.setText(s(R.string.status_loading_weather));

        String path = "/weather/coordinates?lat=" + encodeDouble(lat) + "&lon=" + encodeDouble(lon);
        api("GET", path, null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(weatherCoordinatesButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    lastWeatherCity = displayCity(json.optString("city"));
                    fillCityFields(lastWeatherCity);
                    renderWeatherCard(json, lastWeatherCity);
                    weatherOutput.setText(weatherStatus(json, lastWeatherCity));
                } catch (Exception ex) {
                    weatherOutput.setText(s(R.string.error_weather_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(weatherCoordinatesButton, false);
                showError(weatherOutput, message);
            }
        });
    }

    private void useLocationForWeather() {
        requestCurrentLocation(LOCATION_TARGET_WEATHER);
    }

    private void useLocationForRecommendation() {
        requestCurrentLocation(LOCATION_TARGET_RECOMMENDATION);
    }

    private void prepareRecommendationFromWeather() {
        String city = !lastWeatherCity.trim().isEmpty() ? lastWeatherCity : value(weatherCityInput);
        if (!city.trim().isEmpty()) {
            recommendationCityInput.setText(city.trim());
            clearFieldError(recommendationCityInput);
        }

        showScreen(recommendationScreen);
        recommendationOutput.setText(city.trim().isEmpty()
                ? s(R.string.status_prepare_recommendation_empty)
                : s(R.string.status_prepare_recommendation_ready, city.trim()));
    }

    private void viewRecommendationHistory() {
        showScreen(historyScreen);
        loadHistory();
    }

    private void requestCurrentLocation(int target) {
        TextView output = locationOutputForTarget(target);
        Button button = locationButtonForTarget(target);
        if (!requireToken(output)) {
            return;
        }

        pendingLocationTarget = target;
        setLoading(button, true);
        output.setText(s(R.string.location_loading));

        if (!hasLocationPermission()) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        loadCurrentLocation(target);
    }

    private void loadCurrentLocation(int target) {
        TextView output = locationOutputForTarget(target);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            finishLocationRequest(target);
            showError(output, s(R.string.location_unavailable));
            return;
        }

        try {
            Location cachedLocation = bestLastKnownLocation(locationManager);
            if (isFreshLocation(cachedLocation)) {
                handleLocationFound(target, cachedLocation);
                return;
            }

            String provider = enabledLocationProvider(locationManager);
            if (provider == null) {
                finishLocationRequest(target);
                if (cachedLocation != null) {
                    handleLocationFound(target, cachedLocation);
                } else {
                    showError(output, s(R.string.location_provider_disabled));
                }
                return;
            }

            requestSingleLocationUpdate(target, locationManager, provider, cachedLocation);
        } catch (SecurityException ex) {
            finishLocationRequest(target);
            showError(output, s(R.string.location_permission_denied));
        }
    }

    private void requestSingleLocationUpdate(
            int target,
            LocationManager locationManager,
            String provider,
            Location fallbackLocation
    ) {
        Handler handler = new Handler(Looper.getMainLooper());
        boolean[] completed = {false};

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (completed[0]) {
                    return;
                }

                completed[0] = true;
                handler.removeCallbacksAndMessages(null);
                locationManager.removeUpdates(this);
                handleLocationFound(target, location);
            }

            @Override
            public void onProviderDisabled(String disabledProvider) {
                if (!provider.equals(disabledProvider) || completed[0]) {
                    return;
                }

                completed[0] = true;
                handler.removeCallbacksAndMessages(null);
                locationManager.removeUpdates(this);
                if (fallbackLocation != null) {
                    handleLocationFound(target, fallbackLocation);
                } else {
                    finishLocationRequest(target);
                    showError(locationOutputForTarget(target), s(R.string.location_provider_disabled));
                }
            }
        };

        Runnable timeout = () -> {
            if (completed[0]) {
                return;
            }

            completed[0] = true;
            locationManager.removeUpdates(listener);
            if (fallbackLocation != null) {
                handleLocationFound(target, fallbackLocation);
            } else {
                finishLocationRequest(target);
                showError(locationOutputForTarget(target), s(R.string.location_unavailable));
            }
        };

        handler.postDelayed(timeout, 10000);
        locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper());
    }

    private void handleLocationFound(int target, Location location) {
        if (location == null) {
            finishLocationRequest(target);
            showError(locationOutputForTarget(target), s(R.string.location_unavailable));
            return;
        }

        fillCoordinateFields(location);
        TextView output = locationOutputForTarget(target);
        output.setText(s(
                R.string.status_location_ready,
                formatCoordinate(location.getLatitude()),
                formatCoordinate(location.getLongitude())
        ));
        finishLocationRequest(target);

        if (target == LOCATION_TARGET_WEATHER) {
            getWeatherByCoordinates();
        } else if (target == LOCATION_TARGET_RECOMMENDATION) {
            getRecommendationByCoordinates();
        }
    }

    private void fillCoordinateFields(Location location) {
        String latitude = formatCoordinate(location.getLatitude());
        String longitude = formatCoordinate(location.getLongitude());

        weatherLatInput.setText(latitude);
        weatherLonInput.setText(longitude);
        recommendationLatInput.setText(latitude);
        recommendationLonInput.setText(longitude);
        clearFieldError(weatherLatInput);
        clearFieldError(weatherLonInput);
        clearFieldError(recommendationLatInput);
        clearFieldError(recommendationLonInput);
    }

    private void finishLocationRequest(int target) {
        setLoading(locationButtonForTarget(target), false);
        if (pendingLocationTarget == target) {
            pendingLocationTarget = LOCATION_TARGET_NONE;
        }
    }

    private boolean hasLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationPermissionGranted(int[] grantResults) {
        if (grantResults == null) {
            return false;
        }

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private Location bestLastKnownLocation(LocationManager locationManager) {
        Location bestLocation = null;
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (isBetterLocation(location, bestLocation)) {
                    bestLocation = location;
                }
            } catch (SecurityException ignored) {
                return null;
            }
        }
        return bestLocation;
    }

    private boolean isFreshLocation(Location location) {
        if (location == null) {
            return false;
        }

        long ageMs = System.currentTimeMillis() - location.getTime();
        return ageMs >= 0 && ageMs <= 10 * 60 * 1000;
    }

    private boolean isBetterLocation(Location location, Location currentBest) {
        if (location == null) {
            return false;
        }
        if (currentBest == null) {
            return true;
        }

        boolean newer = location.getTime() > currentBest.getTime();
        boolean moreAccurate = location.hasAccuracy()
                && (!currentBest.hasAccuracy() || location.getAccuracy() < currentBest.getAccuracy());
        return newer || moreAccurate;
    }

    private String enabledLocationProvider(LocationManager locationManager) {
        if (isProviderEnabled(locationManager, LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }
        if (isProviderEnabled(locationManager, LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        }
        if (isProviderEnabled(locationManager, LocationManager.PASSIVE_PROVIDER)) {
            return LocationManager.PASSIVE_PROVIDER;
        }
        return null;
    }

    private boolean isProviderEnabled(LocationManager locationManager, String provider) {
        try {
            return locationManager.isProviderEnabled(provider);
        } catch (Exception ignored) {
            return false;
        }
    }

    private Button locationButtonForTarget(int target) {
        return target == LOCATION_TARGET_RECOMMENDATION
                ? recommendationLocationButton
                : weatherLocationButton;
    }

    private TextView locationOutputForTarget(int target) {
        return target == LOCATION_TARGET_RECOMMENDATION
                ? recommendationOutput
                : weatherOutput;
    }

    private void getRecommendationByCity() {
        requestRecommendation(false);
    }

    private void getRecommendationByCoordinates() {
        requestRecommendation(true);
    }

    private void requestRecommendation(boolean coordinates) {
        if (!requireToken(recommendationOutput)) {
            return;
        }

        JSONObject body = new JSONObject();
        String occasion = value(occasionInput);
        if (!occasion.isEmpty()) {
            put(body, "occasion", occasion);
        }

        if (coordinates) {
            Double lat = parseCoordinate(recommendationLatInput, s(R.string.label_latitude), -90, 90, recommendationOutput);
            if (lat == null) {
                return;
            }
            Double lon = parseCoordinate(recommendationLonInput, s(R.string.label_longitude), -180, 180, recommendationOutput);
            if (lon == null) {
                return;
            }
            put(body, "latitude", lat);
            put(body, "longitude", lon);
        } else {
            String city = value(recommendationCityInput);
            clearFieldError(recommendationCityInput);
            if (city.isEmpty() && !lastWeatherCity.trim().isEmpty()) {
                city = lastWeatherCity;
                recommendationCityInput.setText(city);
            }

            if (city.isEmpty()) {
                showFieldError(
                        recommendationCityInput,
                        recommendationOutput,
                        s(R.string.error_enter_city_or_coordinates)
                );
                return;
            }
            put(body, "city", city);
        }

        Button sourceButton = coordinates ? recommendationCoordinatesButton : recommendationButton;
        setLoading(sourceButton, true);
        recommendationOutput.setText(s(R.string.status_getting_recommendation));

        api("POST", "/recommendations", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(sourceButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    lastWeatherCity = displayCity(json.optString("city", lastWeatherCity));
                    fillCityFields(lastWeatherCity);
                    renderRecommendationCard(json);
                    recommendationOutput.setText(recommendationStatus(json));
                    loadHistory();
                } catch (Exception ex) {
                    recommendationOutput.setText(s(R.string.error_recommendation_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(sourceButton, false);
                showError(recommendationOutput, message);
            }
        });
    }

    private void loadPreferences() {
        if (!requireToken(preferencesOutput)) {
            return;
        }

        setLoading(preferencesLoadButton, true);
        preferencesOutput.setText(s(R.string.status_loading_preferences));

        api("GET", "/preferences", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(preferencesLoadButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    applyPreferenceResponse(json);
                    preferencesOutput.setText(formatPreference(json));
                } catch (Exception ex) {
                    preferencesOutput.setText(s(R.string.error_preferences_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(preferencesLoadButton, false);
                showError(preferencesOutput, message);
            }
        });
    }

    private void savePreferences() {
        if (!requireToken(preferencesOutput)) {
            return;
        }

        JSONObject body = buildPreferenceRequest();
        if (body == null) {
            return;
        }

        setLoading(preferencesSaveButton, true);
        preferencesOutput.setText(s(R.string.status_saving_preferences));

        api("PUT", "/preferences", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(preferencesSaveButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    applyPreferenceResponse(json);
                    preferencesOutput.setText(s(R.string.status_preferences_saved, formatPreference(json)));
                } catch (Exception ex) {
                    preferencesOutput.setText(s(R.string.error_preferences_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(preferencesSaveButton, false);
                showError(preferencesOutput, message);
            }
        });
    }

    private JSONObject buildPreferenceRequest() {
        String style = selectedSpinnerValue(stylePreferenceInput);
        String cold = selectedSpinnerValue(coldSensitivityInput);
        String heat = selectedSpinnerValue(heatSensitivityInput);
        String wind = selectedSpinnerValue(windSensitivityInput);
        String rain = selectedSpinnerValue(rainSensitivityInput);
        String activity = selectedSpinnerValue(activityLevelInput);
        Integer maxLayers = parseOptionalInt(maxLayersInput, s(R.string.label_max_layers), 1, 5, preferencesOutput);
        if (maxLayers == null && value(maxLayersInput).isEmpty()) {
            maxLayers = 3;
        }

        if (maxLayers == null) {
            return null;
        }

        JSONObject body = new JSONObject();
        put(body, "stylePreference", style);
        put(body, "coldSensitivity", cold);
        put(body, "heatSensitivity", heat);
        put(body, "windSensitivity", wind);
        put(body, "rainSensitivity", rain);
        put(body, "maxLayers", maxLayers);
        put(body, "prefersHeadwear", prefersHeadwearInput.isChecked());
        put(body, "prefersWaterproof", prefersWaterproofInput.isChecked());
        put(body, "activityLevel", activity);
        put(body, "preferredColors", value(preferredColorsInput));
        put(body, "avoidItems", value(avoidItemsInput));
        return body;
    }

    private void sendChatMessage() {
        if (!requireToken(chatOutput)) {
            return;
        }

        String message = value(chatMessageInput);
        clearFieldError(chatMessageInput);
        if (message.isEmpty()) {
            showFieldError(chatMessageInput, chatOutput, s(R.string.error_enter_message));
            return;
        }

        if (message.length() > 3000) {
            showFieldError(chatMessageInput, chatOutput, s(R.string.error_message_too_long));
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "message", message);
        putIfPresent(body, "city", value(chatCityInput));

        Long sessionId = parseOptionalLong(chatSessionInput, s(R.string.label_session_id), chatOutput);
        if (sessionId == null && !value(chatSessionInput).isEmpty()) {
            return;
        }
        if (sessionId != null) {
            put(body, "sessionId", sessionId);
        } else if (chatSessionId != null) {
            put(body, "sessionId", chatSessionId);
        }

        setLoading(chatSendButton, true);
        chatOutput.setText(s(R.string.status_sending_chat));
        String submittedMessage = message;

        api("POST", "/chat", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(chatSendButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    if (!json.isNull("sessionId")) {
                        chatSessionId = json.getLong("sessionId");
                        chatSessionInput.setText(String.valueOf(chatSessionId));
                    }
                    chatMessageInput.setText("");
                    renderChatAnswer(submittedMessage, json);
                    updateChatSessionView();
                    chatOutput.setText(s(R.string.status_chat_sent, String.valueOf(chatSessionId)));
                } catch (Exception ex) {
                    chatOutput.setText(s(R.string.error_chat_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(chatSendButton, false);
                showError(chatOutput, message);
            }
        });
    }

    private void newChat() {
        chatSessionId = null;
        chatSessionInput.setText("");
        chatShowingMessages = true;
        resetList(chatListPanel, s(R.string.chat_empty));
        updateChatSessionView();
        chatOutput.setText(s(R.string.status_new_chat));
    }

    private void loadChatSessions() {
        if (!requireToken(chatOutput)) {
            return;
        }

        setLoading(chatLoadSessionsButton, true);
        chatOutput.setText(s(R.string.status_loading_sessions));

        api("GET", "/chat/sessions", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(chatLoadSessionsButton, false);
                try {
                    renderChatSessions(new JSONArray(response));
                    chatOutput.setText(s(R.string.status_chat_sessions_loaded));
                } catch (Exception ex) {
                    chatOutput.setText(s(R.string.error_chat_sessions_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(chatLoadSessionsButton, false);
                showError(chatOutput, message);
            }
        });
    }

    private void loadChatMessages() {
        if (!requireToken(chatOutput)) {
            return;
        }

        Long sessionId = requiredLong(chatSessionInput, s(R.string.label_session_id), chatOutput);
        if (sessionId == null) {
            return;
        }

        setLoading(chatLoadMessagesButton, true);
        chatOutput.setText(s(R.string.status_loading_messages));

        api("GET", "/chat/sessions/" + sessionId, null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(chatLoadMessagesButton, false);
                try {
                    chatSessionId = sessionId;
                    chatSessionInput.setText(String.valueOf(sessionId));
                    updateChatSessionView();
                    renderChatMessages(new JSONArray(response));
                    chatOutput.setText(s(R.string.status_messages_loaded, String.valueOf(sessionId)));
                } catch (Exception ex) {
                    chatOutput.setText(s(R.string.error_chat_messages_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(chatLoadMessagesButton, false);
                showError(chatOutput, message);
            }
        });
    }

    private void confirmDeleteChatSession() {
        if (!requireToken(chatOutput)) {
            return;
        }

        Long sessionId = requiredLong(chatSessionInput, s(R.string.label_session_id), chatOutput);
        if (sessionId == null) {
            return;
        }

        confirm(
                s(R.string.confirm_delete_chat_session_title),
                s(R.string.confirm_delete_chat_session_message),
                () -> deleteChatSession(sessionId)
        );
    }

    private void deleteChatSession(Long sessionId) {
        setLoading(chatDeleteSessionButton, true);
        chatOutput.setText(s(R.string.status_deleting_session));

        api("DELETE", "/chat/sessions/" + sessionId, null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(chatDeleteSessionButton, false);
                if (sessionId.equals(chatSessionId)) {
                    newChat();
                } else {
                    resetList(chatListPanel, s(R.string.status_session_deleted));
                }
                updateChatSessionView();
                chatOutput.setText(s(R.string.status_chat_session_deleted));
            }

            @Override
            public void onError(String message) {
                setLoading(chatDeleteSessionButton, false);
                showError(chatOutput, message);
            }
        });
    }

    private void loadHistory() {
        if (!requireToken(historyOutput)) {
            return;
        }

        setLoading(historyLoadButton, true);
        historyOutput.setText(s(R.string.status_loading_history));

        api("GET", "/history", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(historyLoadButton, false);
                try {
                    renderHistoryList(new JSONArray(response));
                    historyOutput.setText(s(R.string.status_history_loaded));
                } catch (Exception ex) {
                    historyOutput.setText(s(R.string.error_history_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(historyLoadButton, false);
                showError(historyOutput, message);
            }
        });
    }

    private void loadHistoryDetail() {
        if (!requireToken(historyOutput)) {
            return;
        }

        Long historyId = requiredLong(historyItemIdInput, s(R.string.label_history_item_id), historyOutput);
        if (historyId == null) {
            return;
        }

        loadHistoryDetail(historyId);
    }

    private void loadHistoryDetail(Long historyId) {
        setLoading(historyDetailButton, true);
        historyOutput.setText(s(R.string.status_loading_history_item));

        api("GET", "/history/" + historyId, null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(historyDetailButton, false);
                try {
                    renderHistoryDetail(new JSONObject(response));
                    historyOutput.setText(s(R.string.status_history_item_loaded));
                } catch (Exception ex) {
                    historyOutput.setText(s(R.string.error_history_item_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(historyDetailButton, false);
                showError(historyOutput, message);
            }
        });
    }

    private void confirmClearHistory() {
        if (!requireToken(historyOutput)) {
            return;
        }

        confirm(
                s(R.string.confirm_clear_history_title),
                s(R.string.confirm_clear_history_message),
                this::clearHistory
        );
    }

    private void clearHistory() {
        setLoading(historyClearButton, true);
        historyOutput.setText(s(R.string.status_clearing_history));

        api("DELETE", "/history", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(historyClearButton, false);
                resetList(historyListPanel, s(R.string.empty_history));
                selectedHistoryId = null;
                showHistoryListMode();
                historyOutput.setText(s(R.string.status_history_cleared));
            }

            @Override
            public void onError(String message) {
                setLoading(historyClearButton, false);
                showError(historyOutput, message);
            }
        });
    }

    private void submitFeedback() {
        if (!requireToken(feedbackOutput)) {
            return;
        }

        Long historyId = requiredLong(
                feedbackHistoryIdInput,
                s(R.string.label_recommendation_history_id),
                feedbackOutput
        );
        if (historyId == null) {
            return;
        }

        String type = selectedSpinnerValue(feedbackTypeInput);
        boolean hasRating = selectedFeedbackRating > 0;
        String comment = value(feedbackCommentInput);

        if (!hasRating && comment.isEmpty()) {
            showFieldError(feedbackCommentInput, feedbackOutput, s(R.string.error_enter_rating_or_comment));
            return;
        }

        JSONObject body = new JSONObject();
        put(body, "recommendationHistoryId", historyId);
        put(body, "feedbackType", type);
        if (hasRating) {
            put(body, "rating", selectedFeedbackRating);
        }
        putIfPresent(body, "comment", comment);

        setLoading(feedbackSubmitButton, true);
        feedbackOutput.setText(s(R.string.status_submitting_feedback));

        api("POST", "/feedback", body, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(feedbackSubmitButton, false);
                try {
                    JSONObject json = new JSONObject(response);
                    renderFeedbackSingle(json, s(R.string.feedback_saved_title));
                    feedbackCommentInput.setText("");
                    feedbackOutput.setText(s(R.string.status_feedback_saved_for, String.valueOf(historyId)));
                } catch (Exception ex) {
                    feedbackOutput.setText(s(R.string.error_feedback_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(feedbackSubmitButton, false);
                showError(feedbackOutput, message);
            }
        });
    }

    private void loadFeedback() {
        if (!requireToken(feedbackOutput)) {
            return;
        }

        setLoading(feedbackLoadButton, true);
        feedbackOutput.setText(s(R.string.status_loading_feedback));

        api("GET", "/feedback", null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(feedbackLoadButton, false);
                try {
                    renderFeedbackList(new JSONArray(response));
                    feedbackOutput.setText(s(R.string.status_feedback_loaded));
                } catch (Exception ex) {
                    feedbackOutput.setText(s(R.string.error_feedback_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(feedbackLoadButton, false);
                showError(feedbackOutput, message);
            }
        });
    }

    private void loadFeedbackForRecommendation() {
        if (!requireToken(feedbackOutput)) {
            return;
        }

        Long historyId = requiredLong(
                feedbackHistoryIdInput,
                s(R.string.label_recommendation_history_id),
                feedbackOutput
        );
        if (historyId == null) {
            return;
        }

        setLoading(feedbackLoadForRecommendationButton, true);
        feedbackOutput.setText(s(R.string.status_loading_feedback));

        api("GET", "/feedback/recommendations/" + historyId, null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(feedbackLoadForRecommendationButton, false);
                try {
                    renderFeedbackList(new JSONArray(response));
                    feedbackOutput.setText(s(R.string.status_feedback_loaded_for, String.valueOf(historyId)));
                } catch (Exception ex) {
                    feedbackOutput.setText(s(R.string.error_feedback_response, ex.getMessage()));
                }
            }

            @Override
            public void onError(String message) {
                setLoading(feedbackLoadForRecommendationButton, false);
                showError(feedbackOutput, message);
            }
        });
    }

    private void confirmDeleteFeedback() {
        if (!requireToken(feedbackOutput)) {
            return;
        }

        Long feedbackId = requiredLong(feedbackDeleteIdInput, s(R.string.label_feedback_id), feedbackOutput);
        if (feedbackId == null) {
            return;
        }

        confirm(
                s(R.string.confirm_delete_feedback_title),
                s(R.string.confirm_delete_feedback_message),
                () -> deleteFeedback(feedbackId)
        );
    }

    private void deleteFeedback(Long feedbackId) {
        setLoading(feedbackDeleteButton, true);
        feedbackOutput.setText(s(R.string.status_deleting_feedback));

        api("DELETE", "/feedback/" + feedbackId, null, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                setLoading(feedbackDeleteButton, false);
                resetList(feedbackListPanel, s(R.string.status_feedback_deleted_reload));
                feedbackOutput.setText(s(R.string.status_feedback_deleted));
            }

            @Override
            public void onError(String message) {
                setLoading(feedbackDeleteButton, false);
                showError(feedbackOutput, message);
            }
        });
    }

    private void clearToken() {
        token = "";
        chatSessionId = null;
        preferences.edit().remove(KEY_TOKEN).apply();
        updateStatus();
        authOutput.setText(s(R.string.status_token_cleared));
        showScreen(loginScreen);
    }

    private void api(String method, String path, JSONObject body, ApiCallback callback) {
        String requestToken = path.startsWith("/auth/") || path.equals("/health") ? "" : token;
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
        String baseUrl = normalizeBaseUrl(preferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL));
        preferences.edit().putString(KEY_BASE_URL, baseUrl).apply();
        return baseUrl;
    }

    private String normalizeBaseUrl(String baseUrl) {
        String value = baseUrl == null || baseUrl.trim().isEmpty()
                ? DEFAULT_BASE_URL
                : baseUrl.trim();

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private void updateStatus() {
        boolean signedIn = hasToken();
        statusChip.setText(signedIn ? s(R.string.status_signed_in) : s(R.string.status_signed_out));
        statusChip.setTextColor(signedIn ? COLOR_PRIMARY_DARK : COLOR_DANGER);
        statusChip.setBackground(chipBackground(
                signedIn ? COLOR_STATUS_SIGNED_FILL : COLOR_STATUS_SIGNED_OUT_FILL,
                signedIn ? COLOR_STATUS_SIGNED_BORDER : COLOR_STATUS_SIGNED_OUT_BORDER
        ));
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
        if (signOutButton != null) {
            signOutButton.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
        if (deleteAccountButton != null) {
            deleteAccountButton.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
        if (profileToolsLabel != null) {
            profileToolsLabel.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
        if (preferencesShortcutButton != null) {
            preferencesShortcutButton.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
        if (feedbackShortcutButton != null) {
            feedbackShortcutButton.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        }
    }

    private boolean hasToken() {
        return token != null && !token.trim().isEmpty();
    }

    private boolean requireToken(TextView target) {
        if (hasToken()) {
            return true;
        }

        String message = s(R.string.error_auth_required);
        showError(target, message);
        authOutput.setText(message);
        showScreen(loginScreen);
        return false;
    }

    private void showError(TextView target, String message) {
        if (target != null) {
            target.setText(message);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showFieldError(EditText input, TextView target, String message) {
        if (input != null) {
            input.setError(message);
            input.requestFocus();
        }
        showError(target, message);
    }

    private void clearFieldError(EditText input) {
        if (input != null) {
            input.setError(null);
        }
    }

    private void fillCityFields(String city) {
        if (city == null || city.trim().isEmpty()) {
            return;
        }
        if (recommendationCityInput != null && value(recommendationCityInput).isEmpty()) {
            recommendationCityInput.setText(city);
        }
        if (chatCityInput != null && value(chatCityInput).isEmpty()) {
            chatCityInput.setText(city);
        }
    }

    private void renderWeatherCard(JSONObject json, String fallbackCity) {
        if (weatherCardPanel == null) {
            return;
        }

        String condition = json.optString("condition", "UNKNOWN");
        int fillColor = weatherFillColor(json);
        int borderColor = weatherBorderColor(json);
        weatherCardPanel.setBackground(panelBackground(
                fillColor,
                borderColor
        ));

        String city = displayCity(json.optString("city", fallbackCity));
        weatherCardIcon.setText(weatherIconFor(condition));
        weatherCardCity.setText(city.isEmpty() ? s(R.string.weather_selected_location) : city);
        weatherCardTemperature.setText(metricFromJson(json, "temperature", " C"));
        weatherCardCondition.setText(condition);
        weatherCardCondition.setBackground(chipBackground(COLOR_SURFACE, borderColor));
        weatherCardFeels.setText(metricFromJson(json, "feelsLike", " C"));
        weatherCardWind.setText(metricFromJson(json, "windSpeed", " m/s"));
        weatherCardHumidity.setText(json.has("humidity") && !json.isNull("humidity")
                ? json.optInt("humidity") + "%"
                : "--");
        weatherCardPrecipitation.setText(metricFromJson(json, "precipitation", ""));
        weatherCardCached.setText(json.optBoolean("cached", false)
                ? s(R.string.weather_cached_data)
                : s(R.string.weather_fresh_data));
    }

    private void renderRecommendationCard(JSONObject json) {
        if (recommendationCardPanel == null) {
            return;
        }

        String city = displayCity(json.optString("city"));
        String weatherSummary = json.optString("weatherSummary");
        String recommendation = json.optString("recommendation");

        recommendationCardCity.setText(city.isEmpty() ? s(R.string.recommendation_default_title) : city);
        recommendationCardWeather.setText(weatherSummary.trim().isEmpty()
                ? s(R.string.weather_context_unavailable)
                : weatherSummary);
        recommendationCardText.setText(recommendation.trim().isEmpty()
                ? s(R.string.recommendation_text_unavailable)
                : recommendation);
    }

    private int weatherFillColor(JSONObject json) {
        String value = json.optString("condition", "").toUpperCase(Locale.US);
        double temperature = json.optDouble("temperature", 12);
        double windSpeed = json.optDouble("windSpeed", 0);
        if (value.contains("RAIN") || value.contains("DRIZZLE") || value.contains("SNOW")
                || value.contains("MIST") || value.contains("FOG") || temperature <= 5) {
            return COLOR_BLUE_FILL;
        }
        if (value.contains("CLEAR")) {
            return COLOR_WARM_FILL;
        }
        if (temperature >= 20) {
            return COLOR_WARM_FILL;
        }
        if (windSpeed >= 8) {
            return COLOR_GREEN_FILL;
        }
        return COLOR_GREEN_FILL;
    }

    private int weatherBorderColor(JSONObject json) {
        String value = json.optString("condition", "").toUpperCase(Locale.US);
        double temperature = json.optDouble("temperature", 12);
        double windSpeed = json.optDouble("windSpeed", 0);
        if (value.contains("RAIN") || value.contains("DRIZZLE") || value.contains("SNOW")
                || value.contains("MIST") || value.contains("FOG") || temperature <= 5) {
            return COLOR_BLUE_BORDER;
        }
        if (value.contains("CLEAR")) {
            return COLOR_WARM_BORDER;
        }
        if (temperature >= 20) {
            return COLOR_WARM_BORDER;
        }
        if (windSpeed >= 8) {
            return COLOR_GREEN_BORDER;
        }
        return COLOR_GREEN_BORDER;
    }

    private String weatherIconFor(String condition) {
        String value = condition == null ? "" : condition.toUpperCase(Locale.US);
        if (value.contains("CLEAR")) {
            return "\u2600";
        }
        if (value.contains("RAIN") || value.contains("DRIZZLE")) {
            return "\u2614";
        }
        if (value.contains("SNOW")) {
            return "\u2744";
        }
        if (value.contains("THUNDER")) {
            return "\u26A1";
        }
        if (value.contains("MIST") || value.contains("FOG")) {
            return "\u25CC";
        }
        return "\u2601";
    }

    private String metricFromJson(JSONObject json, String key, String suffix) {
        if (!json.has(key) || json.isNull(key)) {
            return "--";
        }

        return String.format(Locale.US, "%.1f", json.optDouble(key)) + suffix;
    }

    private String weatherStatus(JSONObject json, String fallbackCity) {
        String city = displayCity(json.optString("city", fallbackCity));
        StringBuilder builder = new StringBuilder(s(R.string.weather_ready_status));
        if (!city.isEmpty()) {
            builder.append("\n").append(city);
        }
        builder.append(" | ")
                .append(metricFromJson(json, "temperature", " C"))
                .append(" | ")
                .append(json.optString("condition", "UNKNOWN"));
        return builder.toString();
    }

    private String recommendationStatus(JSONObject json) {
        String city = displayCity(json.optString("city"));
        StringBuilder builder = new StringBuilder(s(R.string.recommendation_ready_status));
        if (!city.isEmpty()) {
            builder.append("\n").append(city);
        }
        String weatherSummary = json.optString("weatherSummary");
        if (!weatherSummary.trim().isEmpty()) {
            builder.append(" | ").append(weatherSummary);
        }
        return builder.toString();
    }

    private String formatWeather(JSONObject json, String fallbackCity) {
        String city = displayCity(json.optString("city", fallbackCity));
        StringBuilder builder = new StringBuilder();
        builder.append(s(R.string.label_city)).append(": ").append(city).append("\n");
        appendDouble(builder, s(R.string.label_temperature), json, "temperature", " C");
        appendDouble(builder, s(R.string.label_feels_like), json, "feelsLike", " C");
        appendDouble(builder, s(R.string.label_wind), json, "windSpeed", " m/s");
        builder.append(s(R.string.label_humidity)).append(": ").append(json.optInt("humidity")).append("%\n");
        builder.append(s(R.string.label_condition))
                .append(": ")
                .append(json.optString("condition", "UNKNOWN"))
                .append("\n");
        appendDouble(builder, s(R.string.label_precipitation), json, "precipitation", "");
        builder.append(s(R.string.label_cached))
                .append(": ")
                .append(json.optBoolean("cached", false) ? s(R.string.value_yes) : s(R.string.value_no));
        return builder.toString();
    }

    private String formatRecommendation(JSONObject json) {
        StringBuilder builder = new StringBuilder();

        String city = displayCity(json.optString("city"));
        if (!city.trim().isEmpty()) {
            builder.append(s(R.string.label_city)).append(": ").append(city).append("\n\n");
        }

        String weatherSummary = json.optString("weatherSummary");
        if (!weatherSummary.trim().isEmpty()) {
            builder.append(weatherSummary).append("\n\n");
        }

        builder.append(json.optString("recommendation"));
        return builder.toString();
    }

    private String formatPreference(JSONObject json) {
        return s(R.string.label_id) + ": " + json.optLong("id") + "\n"
                + s(R.string.label_style) + ": " + json.optString("stylePreference") + "\n"
                + s(R.string.label_cold_sensitivity) + ": " + json.optString("coldSensitivity") + "\n"
                + s(R.string.label_heat_sensitivity) + ": " + json.optString("heatSensitivity") + "\n"
                + s(R.string.label_wind_sensitivity) + ": " + json.optString("windSensitivity") + "\n"
                + s(R.string.label_rain_sensitivity) + ": " + json.optString("rainSensitivity") + "\n"
                + s(R.string.label_max_layers) + ": " + json.optInt("maxLayers") + "\n"
                + s(R.string.label_prefers_headwear) + ": " + yesNo(json.optBoolean("prefersHeadwear")) + "\n"
                + s(R.string.label_prefers_waterproof) + ": " + yesNo(json.optBoolean("prefersWaterproof")) + "\n"
                + s(R.string.label_activity_level) + ": " + json.optString("activityLevel") + "\n"
                + s(R.string.label_preferred_colors) + ": " + json.optString("preferredColors") + "\n"
                + s(R.string.label_avoid_items) + ": " + json.optString("avoidItems");
    }

    private void applyPreferenceResponse(JSONObject json) {
        selectSpinnerValue(stylePreferenceInput, json.optString("stylePreference", "CASUAL"));
        selectSpinnerValue(coldSensitivityInput, json.optString("coldSensitivity", "MEDIUM"));
        selectSpinnerValue(heatSensitivityInput, json.optString("heatSensitivity", "MEDIUM"));
        selectSpinnerValue(windSensitivityInput, json.optString("windSensitivity", "MEDIUM"));
        selectSpinnerValue(rainSensitivityInput, json.optString("rainSensitivity", "MEDIUM"));
        maxLayersInput.setText(String.valueOf(json.optInt("maxLayers", 3)));
        prefersHeadwearInput.setChecked(json.optBoolean("prefersHeadwear", false));
        prefersWaterproofInput.setChecked(json.optBoolean("prefersWaterproof", false));
        selectSpinnerValue(activityLevelInput, json.optString("activityLevel", "MEDIUM"));
        preferredColorsInput.setText(json.optString("preferredColors", ""));
        avoidItemsInput.setText(json.optString("avoidItems", ""));
        updateColorPreview();
    }

    private void renderHistoryList(JSONArray items) throws Exception {
        showHistoryListMode();
        resetList(historyListPanel, items.length() == 0 ? s(R.string.empty_history) : "");
        for (int index = 0; index < items.length(); index++) {
            addHistoryCard(items.getJSONObject(index));
        }
    }

    private void renderHistoryDetail(JSONObject item) {
        long id = item.optLong("id");
        selectedHistoryId = id;
        historyItemIdInput.setText(String.valueOf(id));
        feedbackHistoryIdInput.setText(String.valueOf(id));

        String city = displayCity(item.optString("city"));
        selectedHistoryCity = city;
        selectedHistoryCreatedAt = item.optString("createdAt");
        selectedHistoryRecommendation = nonBlank(
                item.optString("recommendationText"),
                s(R.string.recommendation_text_unavailable)
        );
        historyDetailTitle.setText(city.isEmpty() ? s(R.string.history_item_title, String.valueOf(id)) : city);
        historyDetailDate.setText(s(R.string.history_item_date, String.valueOf(id), selectedHistoryCreatedAt));
        historyDetailWeather.setText(nonBlank(
                item.optString("weatherSummary"),
                s(R.string.weather_context_unavailable)
        ));
        historyDetailRecommendation.setText(selectedHistoryRecommendation);
        updateFeedbackContext();
        showHistoryDetailMode();
    }

    private void addHistoryCard(JSONObject item) {
        long id = item.optLong("id");
        LinearLayout card = infoCard(
                s(R.string.history_card_title, String.valueOf(id), displayCity(item.optString("city"))),
                item.optString("createdAt"),
                item.optString("weatherSummary") + "\n\n" + item.optString("recommendationText"),
                COLOR_GREEN_FILL,
                COLOR_GREEN_BORDER
        );
        card.setOnClickListener(view -> {
            historyItemIdInput.setText(String.valueOf(id));
            feedbackHistoryIdInput.setText(String.valueOf(id));
            historyOutput.setText(s(R.string.status_opening_history_item, String.valueOf(id)));
            loadHistoryDetail(id);
        });
        historyListPanel.addView(card);
    }

    private void showHistoryListMode() {
        if (historyListLabel != null) {
            historyListLabel.setVisibility(View.VISIBLE);
        }
        if (historyListPanel != null) {
            historyListPanel.setVisibility(View.VISIBLE);
        }
        if (historyDetailPanel != null) {
            historyDetailPanel.setVisibility(View.GONE);
        }
    }

    private void showHistoryDetailMode() {
        if (historyListLabel != null) {
            historyListLabel.setVisibility(View.GONE);
        }
        if (historyListPanel != null) {
            historyListPanel.setVisibility(View.GONE);
        }
        if (historyDetailPanel != null) {
            historyDetailPanel.setVisibility(View.VISIBLE);
        }
    }

    private void backToHistoryList() {
        showHistoryListMode();
        historyOutput.setText(s(R.string.status_back_to_history));
    }

    private void leaveFeedbackForHistoryItem() {
        Long historyId = selectedHistoryId;
        if (historyId == null) {
            historyId = parseOptionalLong(historyItemIdInput, s(R.string.label_history_item_id), historyOutput);
        }
        if (historyId == null) {
            showFieldError(historyItemIdInput, historyOutput, s(R.string.error_open_history_first));
            return;
        }

        feedbackHistoryIdInput.setText(String.valueOf(historyId));
        selectedHistoryId = historyId;
        selectFeedbackType("RATING");
        updateFeedbackContext();
        feedbackOutput.setText(s(R.string.status_feedback_for_recommendation, String.valueOf(historyId)));
        showScreen(feedbackScreen);
    }

    private void updateFeedbackContext() {
        if (feedbackContextPanel == null || feedbackContextTitle == null) {
            return;
        }

        if (selectedHistoryId == null) {
            feedbackContextTitle.setText(s(R.string.feedback_context_empty));
            feedbackContextSubtitle.setText("");
            feedbackContextPreview.setText("");
            return;
        }

        String title = selectedHistoryCity.trim().isEmpty()
                ? s(R.string.history_item_title, String.valueOf(selectedHistoryId))
                : selectedHistoryCity;
        feedbackContextTitle.setText(title);
        feedbackContextSubtitle.setText(s(
                R.string.history_item_date,
                String.valueOf(selectedHistoryId),
                selectedHistoryCreatedAt
        ));
        feedbackContextPreview.setText(compactPreview(selectedHistoryRecommendation, 180));
    }

    private void updateColorPreview() {
        if (preferredColorsPreview == null) {
            return;
        }

        while (preferredColorsPreview.getChildCount() > 1) {
            preferredColorsPreview.removeViewAt(1);
        }

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutParams(matchWrap());

        String[] colors = value(preferredColorsInput).split(",");
        int added = 0;
        for (String rawColor : colors) {
            String colorName = rawColor.trim();
            if (colorName.isEmpty()) {
                continue;
            }

            row.addView(colorChip(colorName));
            added++;
            if (added >= 6) {
                break;
            }
        }

        if (added == 0) {
            TextView empty = text(s(R.string.preferences_colors_empty));
            empty.setTextSize(13);
            empty.setTextColor(COLOR_MUTED);
            empty.setPadding(0, 0, 0, 0);
            row.addView(empty);
        }

        preferredColorsPreview.addView(row);
    }

    private void updateChatSessionView() {
        updateChatSessionView("", "");
    }

    private void updateChatSessionView(String title, String updatedAt) {
        if (chatSessionTitle == null || chatSessionMeta == null) {
            return;
        }

        if (chatSessionId == null) {
            chatSessionTitle.setText(s(R.string.chat_no_session));
            chatSessionMeta.setText(s(R.string.chat_session_hint));
            return;
        }

        String sessionTitle = title == null || title.trim().isEmpty()
                ? s(R.string.chat_session_title, String.valueOf(chatSessionId))
                : title.trim();
        String city = chatCityInput == null ? "" : value(chatCityInput);
        StringBuilder meta = new StringBuilder();
        meta.append(s(R.string.chat_session_title, String.valueOf(chatSessionId)));
        if (updatedAt != null && !updatedAt.trim().isEmpty()) {
            meta.append(s(R.string.chat_session_meta_updated, updatedAt.trim()));
        }
        if (!city.isEmpty()) {
            meta.append(s(R.string.chat_city_context_line, city));
        }

        chatSessionTitle.setText(sessionTitle);
        chatSessionMeta.setText(meta.toString());
    }

    private void updateChatActivityLabel(String value) {
        if (chatActivityLabel != null) {
            chatActivityLabel.setText(value);
        }
    }

    private void renderFeedbackList(JSONArray items) throws Exception {
        resetList(feedbackListPanel, items.length() == 0 ? s(R.string.empty_feedback) : "");
        for (int index = 0; index < items.length(); index++) {
            addFeedbackCard(items.getJSONObject(index));
        }
    }

    private void renderFeedbackSingle(JSONObject item, String title) {
        resetList(feedbackListPanel, "");
        addFeedbackCard(item, title);
    }

    private void addFeedbackCard(JSONObject item) {
        addFeedbackCard(item, s(R.string.feedback_card_title, String.valueOf(item.optLong("id"))));
    }

    private void addFeedbackCard(JSONObject item, String title) {
        long id = item.optLong("id");
        String rating = item.isNull("rating")
                ? s(R.string.feedback_no_rating)
                : s(R.string.feedback_rating_value, String.valueOf(item.optInt("rating")));
        String comment = item.optString("comment");
        LinearLayout card = infoCard(
                title,
                s(
                        R.string.feedback_card_subtitle,
                        item.optString("feedbackType"),
                        rating,
                        String.valueOf(item.optLong("recommendationHistoryId"))
                ),
                comment.trim().isEmpty() ? item.optString("createdAt") : comment + "\n\n" + item.optString("createdAt"),
                COLOR_WARM_FILL,
                COLOR_WARM_BORDER
        );
        card.setOnClickListener(view -> {
            feedbackDeleteIdInput.setText(String.valueOf(id));
            feedbackOutput.setText(s(R.string.status_selected_feedback, String.valueOf(id)));
        });
        feedbackListPanel.addView(card);
    }

    private void renderChatAnswer(String submittedMessage, JSONObject item) {
        if (!chatShowingMessages) {
            resetList(chatListPanel, "");
        } else if (isListShowingOnlyEmptyState(chatListPanel)) {
            resetList(chatListPanel, "");
        }
        chatShowingMessages = true;
        updateChatActivityLabel(s(R.string.chat_conversation));
        addChatMessageCard("USER", submittedMessage, "");
        addChatMessageCard("ASSISTANT", item.optString("answer"), item.optString("createdAt"));
    }

    private void renderChatSessions(JSONArray items) throws Exception {
        chatShowingMessages = false;
        updateChatActivityLabel(s(R.string.chat_sessions));
        resetList(chatListPanel, items.length() == 0 ? s(R.string.chat_sessions_empty) : "");
        for (int index = 0; index < items.length(); index++) {
            JSONObject item = items.getJSONObject(index);
            long id = item.optLong("id");
            LinearLayout card = infoCard(
                    s(
                            R.string.history_card_title,
                            String.valueOf(id),
                            item.optString("title", s(R.string.chat_default_title))
                    ),
                    s(R.string.chat_session_created, item.optString("createdAt")),
                    s(R.string.chat_session_updated, item.optString("updatedAt")),
                    COLOR_BLUE_FILL,
                    COLOR_BLUE_BORDER
            );
            card.setOnClickListener(view -> {
                chatSessionId = id;
                chatSessionInput.setText(String.valueOf(id));
                updateChatSessionView(
                        item.optString("title", s(R.string.chat_default_title)),
                        item.optString("updatedAt")
                );
                chatOutput.setText(s(R.string.status_opening_chat_session, String.valueOf(id)));
                loadChatMessages();
            });
            chatListPanel.addView(card);
        }
    }

    private void renderChatMessages(JSONArray items) throws Exception {
        chatShowingMessages = true;
        updateChatActivityLabel(s(R.string.chat_conversation));
        resetList(chatListPanel, items.length() == 0 ? s(R.string.chat_empty) : "");
        for (int index = 0; index < items.length(); index++) {
            JSONObject item = items.getJSONObject(index);
            addChatMessageCard(
                    item.optString("role", "ASSISTANT"),
                    item.optString("content"),
                    item.optString("createdAt")
            );
        }
    }

    private void addChatMessageCard(String role, String content, String createdAt) {
        boolean userMessage = "USER".equalsIgnoreCase(role);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(userMessage ? Gravity.END : Gravity.START);
        row.setLayoutParams(matchWrap());

        LinearLayout bubble = verticalLayout();
        bubble.setPadding(dp(14), dp(10), dp(14), dp(11));
        bubble.setBackground(panelBackground(
                userMessage ? COLOR_PRIMARY : COLOR_BLUE_FILL,
                userMessage ? COLOR_PRIMARY : COLOR_BLUE_BORDER
        ));

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0.84f
        );
        bubbleParams.setMargins(
                userMessage ? dp(44) : 0,
                0,
                userMessage ? 0 : dp(44),
                dp(10)
        );
        bubble.setLayoutParams(bubbleParams);

        TextView meta = text(userMessage ? s(R.string.chat_user_label) : s(R.string.chat_assistant_label));
        meta.setTextSize(12);
        meta.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        meta.setTextColor(userMessage ? Color.WHITE : COLOR_PRIMARY_DARK);
        meta.setPadding(0, 0, 0, dp(4));
        bubble.addView(meta);

        TextView body = text(content == null ? "" : content);
        body.setTextSize(14);
        body.setTextColor(userMessage ? Color.WHITE : COLOR_TEXT);
        body.setLineSpacing(dp(2), 1.0f);
        body.setPadding(0, 0, 0, 0);
        bubble.addView(body);

        if (createdAt != null && !createdAt.trim().isEmpty()) {
            TextView time = text(createdAt);
            time.setTextSize(11);
            time.setTextColor(userMessage ? Color.WHITE : COLOR_MUTED);
            time.setPadding(0, dp(6), 0, 0);
            bubble.addView(time);
        }

        row.addView(bubble);
        chatListPanel.addView(row);
    }

    private String formatHistoryList(JSONArray items) throws Exception {
        if (items.length() == 0) {
            return s(R.string.empty_history);
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < items.length(); index++) {
            JSONObject item = items.getJSONObject(index);
            builder.append(formatHistoryItem(item));
            if (index < items.length() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    private String formatHistoryItem(JSONObject item) {
        return "#" + item.optLong("id") + " | " + item.optString("createdAt") + "\n"
                + displayCity(item.optString("city")) + "\n"
                + item.optString("weatherSummary") + "\n"
                + item.optString("recommendationText");
    }

    private String formatFeedbackList(JSONArray items) throws Exception {
        if (items.length() == 0) {
            return s(R.string.empty_feedback);
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < items.length(); index++) {
            builder.append(formatFeedbackItem(items.getJSONObject(index)));
            if (index < items.length() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    private String formatFeedbackItem(JSONObject item) {
        StringBuilder builder = new StringBuilder();
        builder.append("#").append(item.optLong("id"))
                .append(" ")
                .append(s(R.string.feedback_for_history, String.valueOf(item.optLong("recommendationHistoryId"))))
                .append("\n")
                .append(item.optString("feedbackType"));
        if (!item.isNull("rating")) {
            builder.append(" | ").append(s(R.string.feedback_rating_value, String.valueOf(item.optInt("rating"))));
        }
        String comment = item.optString("comment");
        if (!comment.trim().isEmpty()) {
            builder.append("\n").append(comment);
        }
        builder.append("\n").append(item.optString("createdAt"));
        return builder.toString();
    }

    private String formatChatSessions(JSONArray items) throws Exception {
        if (items.length() == 0) {
            return s(R.string.chat_sessions_empty);
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < items.length(); index++) {
            JSONObject item = items.getJSONObject(index);
            builder.append("#").append(item.optLong("id"))
                    .append(" | ").append(item.optString("title", s(R.string.chat_default_title)))
                    .append("\n").append(s(R.string.chat_session_created, item.optString("createdAt")))
                    .append("\n").append(s(R.string.chat_session_updated, item.optString("updatedAt")));
            if (index < items.length() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    private String formatChatMessages(JSONArray items) throws Exception {
        if (items.length() == 0) {
            return s(R.string.chat_messages_empty);
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < items.length(); index++) {
            JSONObject item = items.getJSONObject(index);
            builder.append(item.optString("role"))
                    .append(" | ").append(item.optString("createdAt"))
                    .append("\n")
                    .append(item.optString("content"));
            if (index < items.length() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    private String displayCity(String value) {
        return value == null ? "" : value.trim();
    }

    private String nonBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String compactPreview(String value, int maxLength) {
        String text = nonBlank(value, "");
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, Math.max(0, maxLength - 3)).trim() + "...";
    }

    private int colorForPreference(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.US);
        if (normalized.startsWith("#")) {
            try {
                return Color.parseColor(normalized);
            } catch (IllegalArgumentException ignored) {
                return COLOR_SURFACE;
            }
        }

        switch (normalized) {
            case "black":
                return Color.rgb(31, 35, 40);
            case "white":
                return Color.WHITE;
            case "grey":
            case "gray":
                return Color.rgb(121, 130, 139);
            case "navy":
                return Color.rgb(31, 48, 76);
            case "blue":
                return Color.rgb(62, 111, 176);
            case "green":
                return Color.rgb(75, 132, 96);
            case "red":
                return Color.rgb(176, 82, 82);
            case "pink":
                return Color.rgb(205, 112, 150);
            case "purple":
                return Color.rgb(112, 91, 158);
            case "yellow":
                return Color.rgb(224, 188, 82);
            case "orange":
                return Color.rgb(212, 137, 72);
            case "brown":
                return Color.rgb(129, 93, 69);
            case "beige":
            case "cream":
                return Color.rgb(220, 204, 172);
            default:
                return COLOR_SURFACE;
        }
    }

    private int readableTextColor(int backgroundColor) {
        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255;
        return luminance > 0.62 ? COLOR_PRIMARY_DARK : Color.WHITE;
    }

    private void showScreen(LinearLayout visibleScreen) {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(hasToken() ? View.VISIBLE : View.GONE);
        }

        loginScreen.setVisibility(visibleScreen == loginScreen ? View.VISIBLE : View.GONE);
        weatherScreen.setVisibility(visibleScreen == weatherScreen ? View.VISIBLE : View.GONE);
        recommendationScreen.setVisibility(
                visibleScreen == recommendationScreen ? View.VISIBLE : View.GONE
        );
        preferencesScreen.setVisibility(visibleScreen == preferencesScreen ? View.VISIBLE : View.GONE);
        chatScreen.setVisibility(visibleScreen == chatScreen ? View.VISIBLE : View.GONE);
        historyScreen.setVisibility(visibleScreen == historyScreen ? View.VISIBLE : View.GONE);
        feedbackScreen.setVisibility(visibleScreen == feedbackScreen ? View.VISIBLE : View.GONE);

        styleTab(loginTab, visibleScreen == loginScreen
                || visibleScreen == preferencesScreen
                || visibleScreen == feedbackScreen);
        styleTab(weatherTab, visibleScreen == weatherScreen);
        styleTab(recommendationTab, visibleScreen == recommendationScreen);
        styleTab(preferencesTab, visibleScreen == preferencesScreen);
        styleTab(chatTab, visibleScreen == chatScreen);
        styleTab(historyTab, visibleScreen == historyScreen);
        styleTab(feedbackTab, visibleScreen == feedbackScreen);
    }

    private LinearLayout screen(String title) {
        LinearLayout layout = verticalLayout();
        layout.setPadding(0, dp(4), 0, dp(8));

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

    private LinearLayout spinnerField(String label, Spinner spinner) {
        LinearLayout layout = verticalLayout();
        layout.addView(label(label));
        layout.addView(spinner);
        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(12));
        layout.setLayoutParams(params);
        return layout;
    }

    private LinearLayout preferenceStyleCard() {
        LinearLayout card = preferenceCard(
                s(R.string.preferences_card_style),
                COLOR_WARM_FILL,
                COLOR_WARM_BORDER
        );
        card.addView(spinnerField(s(R.string.label_style), stylePreferenceInput));
        card.addView(field(s(R.string.label_preferred_colors), preferredColorsInput));
        preferredColorsPreview = colorPreview();
        card.addView(preferredColorsPreview);
        card.addView(field(s(R.string.label_avoid_items), avoidItemsInput));
        return card;
    }

    private LinearLayout preferenceSensitivityCard() {
        LinearLayout card = preferenceCard(
                s(R.string.preferences_card_sensitivity),
                COLOR_BLUE_FILL,
                COLOR_BLUE_BORDER
        );
        card.addView(spinnerField(s(R.string.label_cold_sensitivity), coldSensitivityInput));
        card.addView(spinnerField(s(R.string.label_heat_sensitivity), heatSensitivityInput));
        card.addView(spinnerField(s(R.string.label_wind_sensitivity), windSensitivityInput));
        card.addView(spinnerField(s(R.string.label_rain_sensitivity), rainSensitivityInput));
        return card;
    }

    private LinearLayout preferenceComfortCard() {
        LinearLayout card = preferenceCard(
                s(R.string.preferences_card_comfort),
                COLOR_GREEN_FILL,
                COLOR_GREEN_BORDER
        );
        card.addView(field(s(R.string.label_max_layers), maxLayersInput));
        card.addView(prefersHeadwearInput);
        card.addView(prefersWaterproofInput);
        card.addView(spinnerField(s(R.string.label_activity_level), activityLevelInput));
        return card;
    }

    private LinearLayout preferenceCard(String title, int fillColor, int borderColor) {
        LinearLayout card = verticalLayout();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(panelBackground(fillColor, borderColor));
        card.setLayoutParams(panelParams());

        TextView titleView = text(title);
        titleView.setTextSize(17);
        titleView.setTextColor(COLOR_PRIMARY_DARK);
        titleView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        titleView.setIncludeFontPadding(false);
        titleView.setPadding(0, 0, 0, dp(12));
        card.addView(titleView);
        return card;
    }

    private LinearLayout colorPreview() {
        LinearLayout layout = verticalLayout();
        layout.addView(label(s(R.string.preferences_color_palette)));

        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(12));
        layout.setLayoutParams(params);
        return layout;
    }

    private LinearLayout chatSessionView() {
        LinearLayout panel = verticalLayout();
        panel.setPadding(dp(16), dp(15), dp(16), dp(16));
        panel.setBackground(panelBackground(COLOR_BLUE_FILL, COLOR_BLUE_BORDER));
        panel.setLayoutParams(panelParams());

        panel.addView(chip(s(R.string.chat_current_session)));

        chatSessionTitle = text(s(R.string.chat_no_session));
        chatSessionTitle.setTextSize(19);
        chatSessionTitle.setTextColor(COLOR_PRIMARY_DARK);
        chatSessionTitle.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        chatSessionTitle.setPadding(0, dp(9), 0, dp(2));
        panel.addView(chatSessionTitle);

        chatSessionMeta = text(s(R.string.chat_session_hint));
        chatSessionMeta.setTextSize(13);
        chatSessionMeta.setTextColor(COLOR_MUTED);
        chatSessionMeta.setLineSpacing(dp(2), 1.0f);
        chatSessionMeta.setPadding(0, 0, 0, 0);
        panel.addView(chatSessionMeta);
        return panel;
    }

    private LinearLayout historyDetailView() {
        LinearLayout panel = verticalLayout();
        panel.setPadding(dp(16), dp(15), dp(16), dp(16));
        panel.setBackground(panelBackground(COLOR_WARM_FILL, COLOR_WARM_BORDER));
        panel.setLayoutParams(panelParams());
        panel.setVisibility(View.GONE);

        panel.addView(chip(s(R.string.history_detail_chip)));

        historyDetailTitle = text(s(R.string.recommendation_default_title));
        historyDetailTitle.setTextSize(21);
        historyDetailTitle.setTextColor(COLOR_PRIMARY_DARK);
        historyDetailTitle.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        historyDetailTitle.setPadding(0, dp(10), 0, dp(2));
        panel.addView(historyDetailTitle);

        historyDetailDate = text("");
        historyDetailDate.setTextSize(12);
        historyDetailDate.setTextColor(COLOR_MUTED);
        historyDetailDate.setPadding(0, 0, 0, dp(12));
        panel.addView(historyDetailDate);

        panel.addView(sectionLabel(s(R.string.history_detail_weather)));
        historyDetailWeather = text("");
        historyDetailWeather.setTextSize(14);
        historyDetailWeather.setTextColor(COLOR_TEXT);
        historyDetailWeather.setLineSpacing(dp(2), 1.0f);
        historyDetailWeather.setPadding(0, 0, 0, dp(12));
        panel.addView(historyDetailWeather);

        panel.addView(sectionLabel(s(R.string.history_detail_recommendation)));
        historyDetailRecommendation = text("");
        historyDetailRecommendation.setTextSize(16);
        historyDetailRecommendation.setTextColor(COLOR_TEXT);
        historyDetailRecommendation.setLineSpacing(dp(3), 1.0f);
        historyDetailRecommendation.setPadding(0, 0, 0, dp(14));
        panel.addView(historyDetailRecommendation);

        LinearLayout actions = horizontalActions();
        historyBackButton = weightedButton(
                secondaryButton(s(R.string.button_back_to_history), this::backToHistoryList)
        );
        historyFeedbackButton = weightedButton(
                primaryButton(s(R.string.button_leave_feedback), this::leaveFeedbackForHistoryItem)
        );
        actions.addView(historyBackButton);
        actions.addView(historyFeedbackButton);
        panel.addView(actions);
        return panel;
    }

    private LinearLayout feedbackContextView() {
        LinearLayout panel = verticalLayout();
        panel.setPadding(dp(16), dp(15), dp(16), dp(16));
        panel.setBackground(panelBackground(COLOR_BLUE_FILL, COLOR_BLUE_BORDER));
        panel.setLayoutParams(panelParams());

        panel.addView(chip(s(R.string.feedback_context_chip)));

        feedbackContextTitle = text(s(R.string.feedback_context_empty));
        feedbackContextTitle.setTextSize(17);
        feedbackContextTitle.setTextColor(COLOR_PRIMARY_DARK);
        feedbackContextTitle.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        feedbackContextTitle.setPadding(0, dp(10), 0, dp(2));
        panel.addView(feedbackContextTitle);

        feedbackContextSubtitle = text("");
        feedbackContextSubtitle.setTextSize(12);
        feedbackContextSubtitle.setTextColor(COLOR_MUTED);
        feedbackContextSubtitle.setPadding(0, 0, 0, dp(8));
        panel.addView(feedbackContextSubtitle);

        feedbackContextPreview = text("");
        feedbackContextPreview.setTextSize(14);
        feedbackContextPreview.setTextColor(COLOR_TEXT);
        feedbackContextPreview.setLineSpacing(dp(2), 1.0f);
        panel.addView(feedbackContextPreview);
        updateFeedbackContext();
        return panel;
    }

    private LinearLayout feedbackTypeSelector() {
        LinearLayout layout = verticalLayout();
        layout.addView(label(s(R.string.feedback_reaction)));

        LinearLayout row = horizontalActions();
        feedbackRatingTypeButton = weightedButton(
                secondaryButton(s(R.string.feedback_type_rating), () -> selectFeedbackType("RATING"))
        );
        feedbackLikeTypeButton = weightedButton(
                secondaryButton(s(R.string.feedback_type_like), () -> selectFeedbackType("LIKE"))
        );
        feedbackDislikeTypeButton = weightedButton(
                secondaryButton(s(R.string.feedback_type_dislike), () -> selectFeedbackType("DISLIKE"))
        );
        row.addView(feedbackRatingTypeButton);
        row.addView(feedbackLikeTypeButton);
        row.addView(feedbackDislikeTypeButton);
        layout.addView(row);

        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(2));
        layout.setLayoutParams(params);
        selectFeedbackType("RATING");
        return layout;
    }

    private LinearLayout listPanel(String emptyText) {
        LinearLayout panel = verticalLayout();
        panel.setLayoutParams(panelParams());
        resetList(panel, emptyText);
        return panel;
    }

    private void resetList(LinearLayout panel, String emptyText) {
        if (panel == null) {
            return;
        }

        panel.removeAllViews();
        if (emptyText != null && !emptyText.trim().isEmpty()) {
            panel.addView(emptyState(emptyText));
        }
    }

    private boolean isListShowingOnlyEmptyState(LinearLayout panel) {
        return panel != null && panel.getChildCount() == 1 && panel.getChildAt(0) instanceof TextView;
    }

    private TextView emptyState(String value) {
        TextView view = text(value);
        view.setTextColor(COLOR_MUTED);
        view.setTextSize(14);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(12), dp(18), dp(12), dp(18));
        view.setBackground(panelBackground(COLOR_OUTPUT_SURFACE, COLOR_SOFT_BORDER));
        view.setLayoutParams(panelParams());
        return view;
    }

    private LinearLayout infoCard(
            String title,
            String subtitle,
            String body,
            int fillColor,
            int borderColor
    ) {
        LinearLayout card = verticalLayout();
        card.setPadding(dp(14), dp(12), dp(14), dp(13));
        card.setBackground(panelBackground(fillColor, borderColor));
        card.setLayoutParams(panelParams());

        TextView titleView = text(title == null ? "" : title);
        titleView.setTextSize(16);
        titleView.setTextColor(COLOR_PRIMARY_DARK);
        titleView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        titleView.setIncludeFontPadding(false);
        card.addView(titleView);

        if (subtitle != null && !subtitle.trim().isEmpty()) {
            TextView subtitleView = text(subtitle);
            subtitleView.setTextSize(12);
            subtitleView.setTextColor(COLOR_MUTED);
            subtitleView.setPadding(0, dp(6), 0, dp(7));
            card.addView(subtitleView);
        }

        if (body != null && !body.trim().isEmpty()) {
            TextView bodyView = text(body);
            bodyView.setTextSize(14);
            bodyView.setTextColor(COLOR_TEXT);
            bodyView.setLineSpacing(dp(2), 1.0f);
            bodyView.setPadding(0, dp(4), 0, 0);
            card.addView(bodyView);
        }

        return card;
    }

    private TextView sectionLabel(String value) {
        TextView view = label(value);
        view.setTextColor(COLOR_PRIMARY_DARK);
        view.setTextSize(12);
        view.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, dp(6), 0, dp(8));
        view.setLayoutParams(params);
        return view;
    }

    private LinearLayout ratingSelector() {
        LinearLayout layout = verticalLayout();
        layout.addView(label(s(R.string.feedback_rating)));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutParams(matchWrap());

        feedbackRatingButtons = new Button[5];
        for (int index = 0; index < feedbackRatingButtons.length; index++) {
            int rating = index + 1;
            Button button = buttonBase("\u2606", () -> chooseFeedbackRating(rating));
            button.setTextSize(21);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    dp(46),
                    1f
            );
            params.setMargins(0, 0, dp(7), 0);
            button.setLayoutParams(params);
            feedbackRatingButtons[index] = button;
            row.addView(button);
        }

        layout.addView(row);
        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(12));
        layout.setLayoutParams(params);
        selectFeedbackRating(selectedFeedbackRating);
        return layout;
    }

    private LinearLayout weatherVisualPanel() {
        weatherCardPanel = verticalLayout();
        weatherCardPanel.setPadding(dp(16), dp(15), dp(16), dp(15));
        weatherCardPanel.setBackground(panelBackground(COLOR_BLUE_FILL, COLOR_BLUE_BORDER));
        weatherCardPanel.setLayoutParams(panelParams());

        LinearLayout headline = new LinearLayout(this);
        headline.setOrientation(LinearLayout.HORIZONTAL);
        headline.setGravity(Gravity.CENTER_VERTICAL);

        weatherCardIcon = text("\u2601");
        weatherCardIcon.setTextSize(38);
        weatherCardIcon.setGravity(Gravity.CENTER);
        weatherCardIcon.setTextColor(COLOR_PRIMARY_DARK);
        weatherCardIcon.setIncludeFontPadding(false);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(52), dp(52));
        iconParams.setMargins(0, 0, dp(12), 0);
        weatherCardIcon.setLayoutParams(iconParams);

        LinearLayout titleGroup = verticalLayout();
        titleGroup.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        weatherCardCity = text(s(R.string.weather_no_weather_yet));
        weatherCardCity.setTextSize(18);
        weatherCardCity.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        weatherCardCity.setIncludeFontPadding(false);
        titleGroup.addView(weatherCardCity);

        weatherCardCondition = chip(s(R.string.weather_waiting));
        titleGroup.addView(weatherCardCondition);

        weatherCardTemperature = text("-- C");
        weatherCardTemperature.setTextSize(38);
        weatherCardTemperature.setTextColor(COLOR_PRIMARY_DARK);
        weatherCardTemperature.setGravity(Gravity.END);
        weatherCardTemperature.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        weatherCardTemperature.setIncludeFontPadding(false);

        headline.addView(weatherCardIcon);
        headline.addView(titleGroup);
        headline.addView(weatherCardTemperature);
        weatherCardPanel.addView(headline);

        LinearLayout firstRow = metricRow();
        weatherCardFeels = metricValue("--");
        weatherCardWind = metricValue("--");
        firstRow.addView(metricBlock(s(R.string.label_feels_like), weatherCardFeels));
        firstRow.addView(metricBlock(s(R.string.label_wind), weatherCardWind));
        weatherCardPanel.addView(firstRow);

        LinearLayout secondRow = metricRow();
        weatherCardHumidity = metricValue("--");
        weatherCardPrecipitation = metricValue("--");
        secondRow.addView(metricBlock(s(R.string.label_humidity), weatherCardHumidity));
        secondRow.addView(metricBlock(s(R.string.label_precipitation), weatherCardPrecipitation));
        weatherCardPanel.addView(secondRow);

        weatherCardCached = chip(s(R.string.weather_cache_status));
        weatherCardPanel.addView(weatherCardCached);
        return weatherCardPanel;
    }

    private LinearLayout recommendationVisualPanel() {
        recommendationCardPanel = verticalLayout();
        recommendationCardPanel.setPadding(dp(16), dp(15), dp(16), dp(16));
        recommendationCardPanel.setBackground(panelBackground(COLOR_WARM_FILL, COLOR_WARM_BORDER));
        recommendationCardPanel.setLayoutParams(panelParams());

        TextView chip = chip(s(R.string.recommendation_outfit_chip));
        recommendationCardPanel.addView(chip);

        recommendationCardCity = text(s(R.string.recommendation_no_recommendation_yet));
        recommendationCardCity.setTextSize(19);
        recommendationCardCity.setTextColor(COLOR_PRIMARY_DARK);
        recommendationCardCity.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        recommendationCardCity.setPadding(0, dp(8), 0, dp(2));
        recommendationCardPanel.addView(recommendationCardCity);

        recommendationCardWeather = text(s(R.string.recommendation_weather_empty));
        recommendationCardWeather.setTextSize(13);
        recommendationCardWeather.setTextColor(COLOR_MUTED);
        recommendationCardWeather.setPadding(0, dp(2), 0, dp(10));
        recommendationCardPanel.addView(recommendationCardWeather);

        recommendationCardText = text(s(R.string.recommendation_text_empty));
        recommendationCardText.setTextSize(16);
        recommendationCardText.setTextColor(COLOR_TEXT);
        recommendationCardText.setLineSpacing(dp(2), 1.0f);
        recommendationCardPanel.addView(recommendationCardText);
        return recommendationCardPanel;
    }

    private LinearLayout metricRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(14), 0, 0);
        row.setLayoutParams(matchWrap());
        return row;
    }

    private LinearLayout metricBlock(String labelText, TextView valueView) {
        LinearLayout block = verticalLayout();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        params.setMargins(0, 0, dp(10), 0);
        block.setLayoutParams(params);

        TextView labelView = label(labelText);
        labelView.setTextSize(12);
        labelView.setPadding(0, 0, 0, dp(2));
        block.addView(labelView);
        block.addView(valueView);
        return block;
    }

    private TextView metricValue(String value) {
        TextView view = text(value);
        view.setTextSize(17);
        view.setTextColor(COLOR_PRIMARY_DARK);
        view.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        view.setIncludeFontPadding(false);
        return view;
    }

    private TextView chip(String value) {
        TextView view = text(value);
        view.setTextSize(12);
        view.setTextColor(COLOR_PRIMARY_DARK);
        view.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        view.setIncludeFontPadding(false);
        view.setPadding(dp(9), dp(5), dp(9), dp(5));
        view.setBackground(chipBackground(COLOR_SURFACE, COLOR_BORDER));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(7), 0, 0);
        view.setLayoutParams(params);
        return view;
    }

    private TextView colorChip(String name) {
        int color = colorForPreference(name);
        TextView view = text(name);
        view.setTextSize(12);
        view.setTextColor(readableTextColor(color));
        view.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        view.setSingleLine(true);
        view.setIncludeFontPadding(false);
        view.setPadding(dp(10), dp(7), dp(10), dp(7));
        view.setBackground(chipBackground(color, COLOR_BORDER));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, dp(8), 0);
        view.setLayoutParams(params);
        return view;
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
        editText.setHintTextColor(COLOR_HINT);
        editText.setSingleLine(true);
        editText.setInputType(password
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                : InputType.TYPE_CLASS_TEXT);
        editText.setMinHeight(dimen(R.dimen.field_height));
        editText.setElevation(dp(0));
        editText.setBackground(panelBackground(COLOR_FIELD, COLOR_BORDER));
        editText.setPadding(dp(12), 0, dp(12), 0);
        editText.setSelectAllOnFocus(false);
        return editText;
    }

    private EditText numberInput(String hint) {
        EditText editText = input(hint, false);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        return editText;
    }

    private EditText decimalInput(String hint) {
        EditText editText = input(hint, false);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);
        return editText;
    }

    private Spinner spinner(String[] values) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setMinimumHeight(dp(48));
        spinner.setPadding(dp(8), 0, dp(8), 0);
        spinner.setBackground(panelBackground(COLOR_FIELD, COLOR_BORDER));
        spinner.setLayoutParams(matchWrap());
        return spinner;
    }

    private CheckBox checkbox(String text) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(text);
        checkBox.setTextColor(COLOR_TEXT);
        checkBox.setTextSize(15);
        checkBox.setButtonTintList(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        checkBox.setPadding(0, 0, 0, dp(8));
        return checkBox;
    }

    private Button navButton(String text, Runnable action) {
        Button button = buttonBase(text, action);
        button.setTextSize(11);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(2), 0, dp(2), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        );
        params.setMargins(dp(3), 0, dp(3), 0);
        button.setLayoutParams(params);
        return button;
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

    private Button dangerButton(String text, Runnable action) {
        Button button = buttonBase(text, action);
        button.setTextColor(Color.WHITE);
        button.setBackground(buttonBackground(COLOR_DANGER, COLOR_DANGER));
        return withFullWidthMargins(button);
    }

    private Button buttonBase(String text, Runnable action) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(dimen(R.dimen.button_height));
        button.setMinimumHeight(dimen(R.dimen.button_height));
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setOnClickListener(view -> action.run());
        return button;
    }

    private Button withFullWidthMargins(Button button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dimen(R.dimen.button_height)
        );
        params.setMargins(0, 0, 0, dp(10));
        button.setLayoutParams(params);
        return button;
    }

    private Button weightedButton(Button button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                dimen(R.dimen.button_height),
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

    private void loadDesignResources() {
        COLOR_BACKGROUND = color(R.color.weatherwear_background);
        COLOR_SURFACE = color(R.color.weatherwear_surface);
        COLOR_OUTPUT_SURFACE = color(R.color.weatherwear_output_surface);
        COLOR_PRIMARY = color(R.color.weatherwear_primary);
        COLOR_PRIMARY_DARK = color(R.color.weatherwear_primary_dark);
        COLOR_TEXT = color(R.color.weatherwear_text);
        COLOR_MUTED = color(R.color.weatherwear_muted);
        COLOR_HINT = color(R.color.weatherwear_hint);
        COLOR_BORDER = color(R.color.weatherwear_border);
        COLOR_SOFT_BORDER = color(R.color.weatherwear_soft_border);
        COLOR_FIELD = color(R.color.weatherwear_field);
        COLOR_DANGER = color(R.color.weatherwear_danger);
        COLOR_SUCCESS = color(R.color.weatherwear_success);
        COLOR_BLUE_FILL = color(R.color.weatherwear_blue_fill);
        COLOR_BLUE_BORDER = color(R.color.weatherwear_blue_border);
        COLOR_GREEN_FILL = color(R.color.weatherwear_green_fill);
        COLOR_GREEN_BORDER = color(R.color.weatherwear_green_border);
        COLOR_WARM_FILL = color(R.color.weatherwear_warm_fill);
        COLOR_WARM_BORDER = color(R.color.weatherwear_warm_border);
        COLOR_STATUS_SIGNED_FILL = color(R.color.weatherwear_status_signed_fill);
        COLOR_STATUS_SIGNED_BORDER = color(R.color.weatherwear_status_signed_border);
        COLOR_STATUS_SIGNED_OUT_FILL = color(R.color.weatherwear_status_signed_out_fill);
        COLOR_STATUS_SIGNED_OUT_BORDER = color(R.color.weatherwear_status_signed_out_border);
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
        button.setAlpha(1f);
        if (loading) {
            loadingOperations++;
        } else if (loadingOperations > 0) {
            loadingOperations--;
        }

        if (loadingSpinner != null) {
            loadingSpinner.setVisibility(loadingOperations > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private GradientDrawable panelBackground(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dimen(R.dimen.card_radius));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private GradientDrawable buttonBackground(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dimen(R.dimen.card_radius));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private GradientDrawable chipBackground(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dimen(R.dimen.chip_radius));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams panelParams() {
        LinearLayout.LayoutParams params = matchWrap();
        params.setMargins(0, 0, 0, dp(12));
        return params;
    }

    private void confirm(String title, String message, Runnable action) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(s(R.string.button_continue), (dialog, which) -> action.run())
                .setNegativeButton(s(R.string.button_cancel), null)
                .show();
    }

    private String value(EditText input) {
        return input.getText().toString().trim();
    }

    private void put(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (Exception ignored) {
        }
    }

    private void putIfPresent(JSONObject json, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            put(json, key, value.trim());
        }
    }

    private String selectedSpinnerValue(Spinner spinner) {
        Object item = spinner.getSelectedItem();
        return item == null ? "" : item.toString();
    }

    private void selectSpinnerValue(Spinner spinner, String value) {
        if (spinner == null || value == null) {
            return;
        }

        for (int index = 0; index < spinner.getCount(); index++) {
            Object item = spinner.getItemAtPosition(index);
            if (item != null && value.equals(item.toString())) {
                spinner.setSelection(index);
                return;
            }
        }
    }

    private void selectFeedbackType(String type) {
        selectSpinnerValue(feedbackTypeInput, type);
        styleTypeButton(feedbackRatingTypeButton, "RATING".equals(type));
        styleTypeButton(feedbackLikeTypeButton, "LIKE".equals(type));
        styleTypeButton(feedbackDislikeTypeButton, "DISLIKE".equals(type));

        if ("LIKE".equals(type)) {
            selectFeedbackRating(5);
        } else if ("DISLIKE".equals(type)) {
            selectFeedbackRating(1);
        } else if (selectedFeedbackRating <= 0) {
            selectFeedbackRating(5);
        }
    }

    private void chooseFeedbackRating(int rating) {
        selectFeedbackType("RATING");
        selectFeedbackRating(rating);
    }

    private void styleTypeButton(Button button, boolean selected) {
        if (button == null) {
            return;
        }

        button.setTextColor(selected ? Color.WHITE : COLOR_PRIMARY_DARK);
        button.setBackground(selected
                ? buttonBackground(COLOR_PRIMARY, COLOR_PRIMARY)
                : buttonBackground(COLOR_SURFACE, COLOR_BORDER));
    }

    private void selectFeedbackRating(int rating) {
        selectedFeedbackRating = rating;
        if (feedbackRatingButtons == null) {
            return;
        }

        for (int index = 0; index < feedbackRatingButtons.length; index++) {
            Button button = feedbackRatingButtons[index];
            boolean selected = index + 1 <= rating;
            button.setText(selected ? "\u2605" : "\u2606");
            button.setTextColor(selected ? Color.WHITE : COLOR_PRIMARY_DARK);
            button.setBackground(selected
                    ? buttonBackground(COLOR_PRIMARY, COLOR_PRIMARY)
                    : buttonBackground(COLOR_SURFACE, COLOR_BORDER));
        }
    }

    private Double parseCoordinate(
            EditText input,
            String label,
            double min,
            double max,
            TextView output
    ) {
        String text = value(input);
        clearFieldError(input);
        if (text.isEmpty()) {
            showFieldError(input, output, s(R.string.error_field_required, label));
            return null;
        }

        try {
            double parsed = Double.parseDouble(text);
            if (parsed < min || parsed > max) {
                showFieldError(
                        input,
                        output,
                        s(R.string.error_field_between, label, String.valueOf(min), String.valueOf(max))
                );
                return null;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            showFieldError(input, output, s(R.string.error_field_valid_number, label));
            return null;
        }
    }

    private Integer parseOptionalInt(
            EditText input,
            String label,
            int min,
            int max,
            TextView output
    ) {
        String text = value(input);
        clearFieldError(input);
        if (text.isEmpty()) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(text);
            if (parsed < min || parsed > max) {
                showFieldError(
                        input,
                        output,
                        s(R.string.error_field_between, label, String.valueOf(min), String.valueOf(max))
                );
                return null;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            showFieldError(input, output, s(R.string.error_field_valid_number, label));
            return null;
        }
    }

    private Long requiredLong(EditText input, String label, TextView output) {
        Long value = parseOptionalLong(input, label, output);
        if (value == null && value(input).isEmpty()) {
            showFieldError(input, output, s(R.string.error_field_required, label));
        }
        return value;
    }

    private Long parseOptionalLong(EditText input, String label, TextView output) {
        String text = value(input);
        clearFieldError(input);
        if (text.isEmpty()) {
            return null;
        }

        try {
            long parsed = Long.parseLong(text);
            if (parsed <= 0) {
                showFieldError(input, output, s(R.string.error_field_greater_than_zero, label));
                return null;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            showFieldError(input, output, s(R.string.error_field_valid_number, label));
            return null;
        }
    }

    private void appendDouble(
            StringBuilder builder,
            String label,
            JSONObject json,
            String key,
            String suffix
    ) {
        if (json.has(key) && !json.isNull(key)) {
            builder.append(label)
                    .append(": ")
                    .append(String.format(Locale.US, "%.1f", json.optDouble(key)))
                    .append(suffix)
                    .append("\n");
        }
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            return value;
        }
    }

    private String encodeDouble(Double value) {
        return String.format(Locale.US, "%.6f", value);
    }

    private String formatCoordinate(double value) {
        return String.format(Locale.US, "%.6f", value);
    }

    private String yesNo(boolean value) {
        return value ? s(R.string.value_yes) : s(R.string.value_no);
    }

    private String s(int resId) {
        return getString(resId);
    }

    private String s(int resId, Object... args) {
        return getString(resId, args);
    }

    private int color(int resId) {
        return getColor(resId);
    }

    private int dimen(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
