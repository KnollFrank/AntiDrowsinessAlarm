package de.drowsydriveralarm.eventproducer;

import com.google.common.base.Optional;

class ActiveState {

    private Optional<Boolean> active = Optional.absent();

    public boolean isUnknown() {
        return !this.active.isPresent();
    }

    public Boolean isActive() {
        return this.active.get();
    }

    public void setActive() {
        this.active = Optional.of(true);
    }

    public boolean isIdle() {
        return !this.isActive();
    }

    public void setIdle() {
        this.active = Optional.of(false);
    }
}
