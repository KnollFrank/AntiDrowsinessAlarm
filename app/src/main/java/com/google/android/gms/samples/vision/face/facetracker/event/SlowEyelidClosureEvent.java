package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class SlowEyelidClosureEvent extends Event {

    private final long duration;

    public SlowEyelidClosureEvent(final long timestampMillis, final long duration) {
        super(timestampMillis);
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SlowEyelidClosureEvent that = (SlowEyelidClosureEvent) o;
        return duration == that.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), duration);
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super
                .getToStringHelper()
                .add("duration", duration);
    }
}
