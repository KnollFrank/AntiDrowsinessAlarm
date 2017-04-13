package de.drowsydriveralarm;

import org.joda.time.Instant;

public class MockedClock implements Clock {

    private Instant now = new Instant(0);

    @Override
    public Instant now() {
        return this.now;
    }

    public void setNow(final Instant now) {
        this.now = now;
    }
}
