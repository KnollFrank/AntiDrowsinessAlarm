package de.drowsydriveralarm;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Instant;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;

class AppIdleCalculator {

    private Duration appIdleDuration = new Duration(0);
    private Optional<AppIdleEvent> idleEventBeforeActiveEvent = Optional.absent();

    public AppIdleCalculator() {
    }

    @Subscribe
    public void onAppIdle(final AppIdleEvent appIdleEvent) {
        this.idleEventBeforeActiveEvent = Optional.of(appIdleEvent);
    }

    @Subscribe
    public void onAppActive(final AppActiveEvent appActiveEvent) {
        this.appIdleDuration = this.appIdleDuration.plus(new Duration(this.idleEventBeforeActiveEvent.get().getInstant(), appActiveEvent.getInstant()));
        this.idleEventBeforeActiveEvent = Optional.absent();
    }

    public Duration getAppIdleDuration(final Instant now) {
        if (this.idleEventBeforeActiveEvent.isPresent()) {
            return this.appIdleDuration.plus(new Duration(this.idleEventBeforeActiveEvent.get().getInstant(), now));
        } else {
            return this.appIdleDuration;
        }
    }
}
