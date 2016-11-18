package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.MoreObjects;

public abstract class Event {

    private final long timestampMillis;

    Event(final long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    public long getTimestampMillis() {
        return this.timestampMillis;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        final Event event=(Event) o;
        return this.timestampMillis == event.timestampMillis;
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.timestampMillis);
    }

    @Override
    public String toString() {
        return this.getToStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return MoreObjects
                .toStringHelper(this)
                .add("timestampMillis", this.timestampMillis);
    }
}
