package com.systematix.itrack.config;

public final class UrlsList {
    public static final String BASE_URL = AppConfig.PRODUCTION
            ? "to be set"
            : "http://192.168.1.5/itrack/";
    public static final String API_URL = BASE_URL + "api";
    // append with 'api...'
    public static final String LOGIN_URL = API_URL + "login";
    public static final String UPLOADED_IMAGES_URL = BASE_URL + "images/";
    public static final String GET_USER_URL = API_URL + "user";
    public static final String GET_MINOR_VIOLATIONS_URL = API_URL + "violation/index/minor";
    public static final String SEND_MINOR_VIOLATION_URL = API_URL + "minorviolation/make";
}
