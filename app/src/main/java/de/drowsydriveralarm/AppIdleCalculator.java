package de.drowsydriveralarm;

import com.google.common.eventbus.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Instant;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;

class AppIdleCalculator {

    private Duration appIdleDuration = new Duration(0);
    private AppIdleEvent appIdleEvent;

    public AppIdleCalculator() {
    }

    @Subscribe
    public void onAppIdle(final AppIdleEvent appIdleEvent) {
        this.appIdleEvent = appIdleEvent;
    }

    @Subscribe
    public void onAppActive(final AppActiveEvent appActiveEvent) {
        this.appIdleDuration = this.appIdleDuration.plus(new Duration(this.appIdleEvent.getInstant(), appActiveEvent.getInstant()));
    }

    public Duration getAppIdleDuration(final Instant now) {
        return this.appIdleDuration;
    }
}
