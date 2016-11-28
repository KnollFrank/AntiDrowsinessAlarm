package de.antidrowsinessalarm.event;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class SlowEyelidClosureEvent extends DurationEvent {

    public SlowEyelidClosureEvent(final Instant instant, final Duration duration) {
        super(instant, duration);
    }
}
