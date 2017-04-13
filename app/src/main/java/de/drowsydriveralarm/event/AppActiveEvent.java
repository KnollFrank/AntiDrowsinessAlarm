package de.drowsydriveralarm.event;

import org.joda.time.Instant;

public class AppActiveEvent extends Event {

    public AppActiveEvent(final Instant instant) {
        super(instant);
    }
}
