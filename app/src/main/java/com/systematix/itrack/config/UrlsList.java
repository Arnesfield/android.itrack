package com.systematix.itrack.config;

public final class UrlsList {
    public static final String BASE_URL = AppConfig.PRODUCTION
            ? "http://feutechitrack.x10host.com/"
            : "http://192.168.43.128/itrack/";
    public static final String API_URL = BASE_URL + "api";
    // append with 'api...'
    public static final String LOGIN_URL = API_URL + "Login";
    public static final String UPLOADED_IMAGES_URL = BASE_URL + "images/";
    public static final String GET_USER_URL = API_URL + "User";
    public static final String GET_VIOLATIONS_URL = API_URL + "Violation";
    public static final String SEND_VIOLATION_URL = API_URL + "Violation/make";
    public static final String SEND_VIOLATION_BATCH_URL = API_URL + "Violation/batch";
    public static final String SEND_USER_FCM_TOKEN_URL = API_URL + "User/token";

    public static String PICTURE_URL(String picture) {
        return BASE_URL + picture;
    }

    public static String GET_ATTENDANCE_HOURS_URL(int uid) {
        return API_URL + "Attendance/hours/" + uid;
    }

    public static String GET_USER_NOTIFICATIONS_URL(int uid) {
        // TODO: make this in api
        return API_URL + "Notifications/user/" + uid;
    }
}
