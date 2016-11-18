package com.google.android.gms.samples.vision.face.facetracker.event;

public class LikelyDrowsyEvent extends DrowsyEventBase {

    public LikelyDrowsyEvent(final long timestampMillis, final double perclos) {
        super(timestampMillis, perclos);
    }
}
