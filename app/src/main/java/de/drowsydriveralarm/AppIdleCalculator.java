package de.drowsydriveralarm;

import android.support.annotation.NonNull;

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
        this.appIdleDuration = this.appIdleDuration.plus(this.getAppIdleDuration(appActiveEvent));
        this.idleEventBeforeActiveEvent = Optional.absent();
    }

    @NonNull
    private Duration getAppIdleDuration(final AppActiveEvent appActiveEvent) {
        return this.getPendingAppIdleDuration(appActiveEvent.getInstant());
    }

    public Duration getAppIdleDuration(final Instant now) {
        return this.idleEventBeforeActiveEvent.isPresent()
                ? this.appIdleDuration.plus(this.getPendingAppIdleDuration(now))
                : this.appIdleDuration;
    }

    @NonNull
    private Duration getPendingAppIdleDuration(final Instant now) {
        return new Duration(this.idleEventBeforeActiveEvent.get().getInstant(), now);
    }
}
