package de.drowsydriveralarm.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.joda.time.Instant;

public abstract class DrowsyEventBase extends Event {

    private final double perclos;

    protected DrowsyEventBase(final Instant instant, final double perclos) {
        super(instant);
        this.perclos = perclos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DrowsyEventBase that = (DrowsyEventBase) o;
        return Double.compare(that.perclos, this.perclos) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), this.perclos);
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super
                .getToStringHelper()
                .add("perclos", this.perclos);
    }
}
