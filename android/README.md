# WeatherWear Android Client

This folder contains a minimal native Android client written in Java. It is intended as a mobile proof for the diploma project and calls the Spring Boot backend over the documented REST API.

## Features

- Login / Register: email/password, JWT retrieval, token persistence.
- Home / Weather: city input, `Get weather`, temperature, wind, humidity, and condition output.
- Recommendation: city plus occasion, `Get recommendation`, LLM answer output.
- Chat: message input, `Send`, assistant answer output.
- History: previous recommendations list and `Clear history`.

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
