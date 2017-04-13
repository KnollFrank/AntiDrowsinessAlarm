package de.drowsydriveralarm.event;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class PendingSlowEyelidClosureEvent extends SlowEyelidClosureEvent {

    public PendingSlowEyelidClosureEvent(final Instant instant, final Duration duration) {
        super(instant, duration);
    }
}
