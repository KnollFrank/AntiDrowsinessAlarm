package de.antidrowsinessalarm.event;

import org.joda.time.Instant;

public class AwakeEvent extends DrowsyEventBase {

    public AwakeEvent(final Instant instant, final double perclos) {
        super(instant, perclos);
    }
}