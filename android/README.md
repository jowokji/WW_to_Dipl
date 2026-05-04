# WeatherWear Android Client

This folder contains a minimal native Android client written in Java. It is intended as a mobile proof for the diploma project and calls the Spring Boot backend over the documented REST API.

## Features

- Register and log in with JWT.
- Save a backend base URL.
- Request weather by city.
- Request weather from the device's last known location.
- Generate clothing recommendations.
- Send messages to the AI style chat.
- Load recommendation history.
- Delete the current account through `DELETE /users/me`.

## Run Locally

1. Start the backend on the host machine:

   ```bash
   docker compose up --build
   ```

2. Open the `android` folder in Android Studio.
3. Run the `app` configuration on an emulator or physical device.
4. Use one of these backend URLs:

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

This repository includes the Android source project, but this environment does not have Gradle or the Android SDK configured. Build verification should be done in Android Studio or with:

```bash
./gradlew :app:assembleDebug
```
