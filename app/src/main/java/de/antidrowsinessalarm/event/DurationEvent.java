package de.antidrowsinessalarm.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

public abstract class DurationEvent extends Event {

    private final Duration duration;

    // TODO: use Interval
    DurationEvent(final Instant instant, final Duration duration) {
        super(instant);
        this.duration = duration;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public Interval getInterval() {
        return new Interval(this.getInstant(), this.getDuration());
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        final DurationEvent that = (DurationEvent) o;
        return Objects.equal(this.duration, that.duration);
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
