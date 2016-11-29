package de.antidrowsinessalarm;

import android.support.annotation.NonNull;

import org.joda.time.Duration;
import org.joda.time.Instant;

class ClockTime2FrameTimeConverter {

    private final Duration delta;

    private ClockTime2FrameTimeConverter(final Instant clockTime, final Instant frameTime) {
        this.delta = new Duration(frameTime.getMillis() - clockTime.getMillis());
    }

    public static ClockTime2FrameTimeConverter fromClockTimeAndFrameTime(final Instant clockTime, final Instant frameTime) {
        return new ClockTime2FrameTimeConverter(clockTime, frameTime);
    }

    @NonNull
    public Instant convertToFrameTime(Instant clockTime) {
        return clockTime.plus(this.delta);
    }
}
