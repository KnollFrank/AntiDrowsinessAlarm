package de.antidrowsinessalarm;

import org.joda.time.Instant;

public class SystemClock implements Clock {

    @Override
    public Instant now() {
        return new Instant();
    }
}
