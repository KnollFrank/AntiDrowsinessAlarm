package de.antidrowsinessalarm.event;

import org.joda.time.Instant;

public class DrowsyEvent extends DrowsyEventBase {

    public DrowsyEvent(final Instant instant, final double perclos) {
        super(instant, perclos);
    }
}
