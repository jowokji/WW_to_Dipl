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

## Local Run

### 1. Start the backend

From the project root, create `.env` if it does not exist yet:

```bash
cp .env.example .env
```

Fill the required local secrets in `.env`, then start the backend:

```bash
docker compose up --build
```

Check that the backend is responding:

```bash
curl http://localhost:8090/api/health
```

Expected result: a JSON response with service status.

### 2. Choose the Android backend URL

Use the Profile screen in the app to save the correct backend URL.

For an Android emulator:

```text
http://10.0.2.2:8090/api
```

For a physical phone on the same Wi-Fi network:

```text
http://<host-lan-ip>:8090/api
```

On Windows, find the host LAN IP with:

```powershell
ipconfig
```

Use the IPv4 address of the active Wi-Fi/Ethernet adapter, for example:

```text
http://192.168.1.25:8090/api
```

If a physical phone cannot connect, check that Docker is running, the backend is still up, both devices are on the same network, and Windows Firewall allows inbound traffic on port `8090`.

### 3. Run from Android Studio

1. Open the `android` folder in Android Studio.
2. Let Android Studio create `local.properties` with your Android SDK path.
3. Select the `app` configuration.
4. Run it on an emulator or physical device.
5. On the Profile screen, set the backend URL and tap `Health`.

The manifest allows cleartext HTTP traffic for local diploma testing. For production, use the deployed HTTPS backend URL and remove cleartext traffic.

## Build APK

After Android Studio or `local.properties` points to a valid SDK, build the debug APK from this folder:

```powershell
.\gradlew.bat --no-problems-report :app:assembleDebug
```

The APK is created at:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

## Quick Troubleshooting

- Backend does not start: run `docker compose ps` and `docker compose logs app`.
- App says it cannot reach the backend: verify the Profile backend URL and tap `Health`.
- Emulator cannot connect to `localhost`: use `http://10.0.2.2:8090/api`.
- Physical phone cannot connect: use the host LAN IP, not `localhost`.
- Auth works but protected screens fail: sign out, log in again, and retry the request.
