package de.antidrowsinessalarm.event;

public class LikelyDrowsyEvent extends DrowsyEventBase {

    public LikelyDrowsyEvent(final long timestampMillis, final double perclos) {
        super(timestampMillis, perclos);
    }
}
