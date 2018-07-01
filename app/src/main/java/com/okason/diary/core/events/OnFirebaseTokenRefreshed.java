package com.okason.diary.core.events;

public class OnFirebaseTokenRefreshed {
    private final String token;

    public OnFirebaseTokenRefreshed(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
