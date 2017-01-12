package org.coderthoughts.recordingservlet;

import java.util.Date;

public class Recording {
    private final String value;
    private final Date timestamp;

    public Recording(String val, Date date) {
        value = val;
        timestamp = date;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getValue() {
        return value;
    }
}
