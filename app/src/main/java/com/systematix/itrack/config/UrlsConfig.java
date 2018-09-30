package com.systematix.itrack.config;

public final class UrlsConfig {
    public static final String BASE_URL = AppConfig.PRODUCTION
            ? "to be set"
            : "http://192.168.1.10/itrack/";
    public static final String API_URL = BASE_URL + "api";
    // append with 'api...'
    public static final String LOGIN_URL = API_URL + "login";
}
