package com.google.android.gms.samples.vision.face.facetracker.event;

public class DrowsyEvent extends Event {

    private final double perclos;

    public DrowsyEvent(final long timestampMillis, final double perclos) {
        super(timestampMillis);
        this.perclos = perclos;
    }
}
