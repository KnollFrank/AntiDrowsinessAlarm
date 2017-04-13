package de.drowsydriveralarm.event;

import org.joda.time.Instant;

public class AppIdleEvent extends Event {

    public AppIdleEvent(final Instant instant) {
        super(instant);
    }
}
