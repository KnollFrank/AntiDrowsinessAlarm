package de.antidrowsinessalarm.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public abstract class DurationEvent extends Event {

    private final long durationMillis;

    // TODO: use data types from Joda-Time for timestampMillis and durationMillis
    DurationEvent(final long timestampMillis, final long durationMillis) {
        super(timestampMillis);
        this.durationMillis = durationMillis;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final DurationEvent that=(DurationEvent) o;
        return this.durationMillis == that.durationMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), this.durationMillis);
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super
                .getToStringHelper()
                .add("durationMillis", this.durationMillis);
    }
}
