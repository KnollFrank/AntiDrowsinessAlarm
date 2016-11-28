package de.antidrowsinessalarm.event;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class NormalEyeBlinkEvent extends DurationEvent {

    public NormalEyeBlinkEvent(final Instant instant, final Duration duration) {
        super(instant, duration);
    }
}
