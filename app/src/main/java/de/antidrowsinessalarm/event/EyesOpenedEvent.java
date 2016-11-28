package de.antidrowsinessalarm.event;

import org.joda.time.Instant;

public class EyesOpenedEvent extends Event {

    public EyesOpenedEvent(final Instant instant) {
        super(instant);
    }
}
