package com.systematix.itrack.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class UnixHelper {
    private long time;

    public UnixHelper(long time) {
        this.time = time;
    }

    public String convert(String format) {
        final Date date = new Date(this.time * 1000L);
        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(date);
    }
}
