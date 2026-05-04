package com.weatherwear.mobile;

interface ApiCallback {

    void onSuccess(String body);

    void onError(String message);
}
