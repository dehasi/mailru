package me.dehasi.highload.internal;

import java.time.Instant;
import java.time.ZoneId;

/** Created by Ravil on 17/12/2018. */
public final class Utils {
    public static int yearFormTs(long ts) {
        return Instant.ofEpochSecond(ts).atZone(ZoneId.of("UTC")).getYear();
    }

    private Utils() {
    }
}
