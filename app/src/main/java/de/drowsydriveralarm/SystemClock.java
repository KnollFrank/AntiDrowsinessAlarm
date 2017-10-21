package de.drowsydriveralarm;

import org.joda.time.Instant;

public class SystemClock implements Clock {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
