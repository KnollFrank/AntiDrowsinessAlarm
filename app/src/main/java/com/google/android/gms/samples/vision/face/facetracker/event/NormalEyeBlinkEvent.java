package com.google.android.gms.samples.vision.face.facetracker.event;

public class NormalEyeBlinkEvent extends DurationEvent {

    public NormalEyeBlinkEvent(final long timestampMillis, final long durationMillis) {
        super(timestampMillis, durationMillis);
    }
}
