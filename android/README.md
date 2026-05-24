# WeatherWear Android Client

This folder contains a native Android client written in Java. It is intended as a mobile demo for the diploma project and calls the Spring Boot backend over the documented REST API.

## Features

- Profile: login/register, JWT persistence, backend URL switching, health check, sign out, and account deletion.
- Weather: city, coordinates, or `Use my location` lookup with a card-style summary and a quick `Recommend outfit` hand-off.
- Recommendation: city, coordinates, or `Use my location` recommendation flow with a dedicated outfit card and `View history` hand-off.
- Preferences: card-based style, weather sensitivity, and comfort settings with a preferred color preview.
- Feedback: selected recommendation preview, star rating, Like/Dislike quick reactions, comment, recommendation-specific feedback lookup, and feedback deletion.
- Chat: current-session card, message bubbles, new chat, session list, message history, and session deletion.
- History: card list of saved recommendations, detail view with weather context and recommendation text, feedback hand-off, and clear-history confirmation.
- Demo UI: bottom navigation for Weather, Recommend, Chat, History, and Profile; shared colors/dimensions/resources; empty states; Toast errors; and a loading spinner for API calls.

## Run Locally

1. Start the backend on the host machine:

   ```bash
   docker compose up --build
   ```

2. Open the `android` folder in Android Studio.
3. Let Android Studio create `local.properties` with your Android SDK path.
4. Run the `app` configuration on an emulator or physical device.
5. Use one of these backend URLs:

   ```text
   http://10.0.2.2:8090/api
   ```

   for the Android emulator, or:

   ```text
   http://<host-lan-ip>:8090/api
   ```

   for a physical device on the same network.

The manifest allows cleartext HTTP traffic for local diploma testing. For production, use the deployed HTTPS backend URL and remove cleartext traffic.

## Verification Notes

This repository includes the Android source project, but this environment does not have the Android SDK configured. Build verification should be done in Android Studio or with an installed Gradle distribution after `ANDROID_HOME` or `android/local.properties` points to a valid SDK:

```bash
gradle :app:assembleDebug
```
