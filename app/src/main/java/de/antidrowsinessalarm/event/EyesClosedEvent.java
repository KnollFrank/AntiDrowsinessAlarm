package de.antidrowsinessalarm.event;

import org.joda.time.Instant;

public class EyesClosedEvent extends Event {

    public EyesClosedEvent(final Instant instant) {
        super(instant);
    }
}
