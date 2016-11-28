package de.antidrowsinessalarm.event;

import org.joda.time.Instant;

public class LikelyDrowsyEvent extends DrowsyEventBase {

    public LikelyDrowsyEvent(final Instant instant, final double perclos) {
        super(instant, perclos);
    }
}
