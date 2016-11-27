package de.antidrowsinessalarm.event;

public class AwakeEvent extends DrowsyEventBase {

    public AwakeEvent(final long timestampMillis, final double perclos) {
        super(timestampMillis, perclos);
    }
}