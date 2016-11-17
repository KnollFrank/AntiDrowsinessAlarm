package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public abstract class Event {

    private final long timestampMillis;

    public Event(final long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return timestampMillis == event.timestampMillis;
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(timestampMillis);
    }

    @Override
    public String toString() {
        return getToStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return MoreObjects
                .toStringHelper(this)
                .add("timestampMillis", timestampMillis);
    }
}
