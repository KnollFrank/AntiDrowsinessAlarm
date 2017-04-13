package de.drowsydriveralarm.event;

import org.joda.time.Instant;

public class EyesOpenedEvent extends Event {

    public EyesOpenedEvent(final Instant instant) {
        super(instant);
    }
}
