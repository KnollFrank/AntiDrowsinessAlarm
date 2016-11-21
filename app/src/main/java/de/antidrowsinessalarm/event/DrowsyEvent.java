package de.antidrowsinessalarm.event;

public class DrowsyEvent extends DrowsyEventBase {

    public DrowsyEvent(final long timestampMillis, final double perclos) {
        super(timestampMillis, perclos);
    }
}
