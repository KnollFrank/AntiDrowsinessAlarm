package de.antidrowsinessalarm.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public abstract class DrowsyEventBase extends Event {

    private final double perclos;

    protected DrowsyEventBase(final long timestampMillis, final double perclos) {
        super(timestampMillis);
        this.perclos = perclos;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
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
