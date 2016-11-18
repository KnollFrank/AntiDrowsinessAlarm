package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public abstract class DurationEvent extends Event {

    private final long duration;

    DurationEvent(final long timestampMillis, final long duration) {
        super(timestampMillis);
        this.duration = duration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final DurationEvent that=(DurationEvent) o;
        return this.duration == that.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), this.duration);
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super
                .getToStringHelper()
                .add("duration", this.duration);
    }
}
