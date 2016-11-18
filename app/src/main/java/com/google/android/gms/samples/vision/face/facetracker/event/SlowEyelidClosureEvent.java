package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class SlowEyelidClosureEvent extends DurationEvent {

    public SlowEyelidClosureEvent(final long timestampMillis, final long duration) {
        super(timestampMillis, duration);
    }
}
