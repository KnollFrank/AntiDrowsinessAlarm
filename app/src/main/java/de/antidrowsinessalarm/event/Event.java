package de.antidrowsinessalarm.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.joda.time.Instant;

public abstract class Event {

    private final Instant instant;

    Event(final Instant instant) {
        this.instant = instant;
    }

    public Instant getInstant() {
        return this.instant;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        final Event event = (Event) o;
        return Objects.equal(this.instant, event.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.instant);
    }

    @Override
    public String toString() {
        return this.getToStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return MoreObjects
                .toStringHelper(this)
                .add("instant", this.instant);
    }
}
