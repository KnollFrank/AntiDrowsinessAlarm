package com.google.android.gms.samples.vision.face.facetracker.event;

public class SlowEyelidClosureEvent extends DurationEvent {

    public SlowEyelidClosureEvent(final long timestampMillis, final long duration) {
        super(timestampMillis, duration);
    }
}
