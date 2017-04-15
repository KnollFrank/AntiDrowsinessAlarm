package de.drowsydriveralarm;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Instant;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.event.Event;

class AppIdleCalculator {

    private Duration appIdleDuration = new Duration(0);
    private Optional<AppIdleEvent> idleEventBeforeActiveEvent = Optional.absent();
    private Optional<AppActiveEvent> appActiveEvent = Optional.absent();

    public AppIdleCalculator() {
    }

    @Subscribe
    public void onAppIdle(final AppIdleEvent appIdleEvent) {
        this.idleEventBeforeActiveEvent = Optional.of(appIdleEvent);
    }

    @Subscribe
    public void onAppActive(final AppActiveEvent appActiveEvent) {
        this.appIdleDuration = this.appIdleDuration.plus(this.getAppIdleDuration(appActiveEvent));
        this.appActiveEvent = Optional.of(appActiveEvent);
        this.idleEventBeforeActiveEvent = Optional.absent();
    }

    public Duration getAppIdleDuration(final Instant now) {
        Preconditions.checkArgument(!this.shallGetAppIdleDurationForUnknownPast(now));

        return this.idleEventBeforeActiveEvent.isPresent()
                ? this.appIdleDuration.plus(this.getPendingAppIdleDuration(now))
                : this.appIdleDuration;
    }

    @NonNull
    private Duration getAppIdleDuration(final AppActiveEvent appActiveEvent) {
        return this.getPendingAppIdleDuration(appActiveEvent.getInstant());
    }

    private boolean shallGetAppIdleDurationForUnknownPast(final Instant now) {
        return this.isNowBeforeEvent(now, this.appActiveEvent) || this.isNowBeforeEvent(now, this.idleEventBeforeActiveEvent);
    }

    private boolean isNowBeforeEvent(final Instant now, final Optional<? extends Event> event) {
        return event.isPresent() && now.isBefore(event.get().getInstant());
    }

    @NonNull
    private Duration getPendingAppIdleDuration(final Instant now) {
        return this.idleEventBeforeActiveEvent.isPresent()
                ? new Duration(this.idleEventBeforeActiveEvent.get().getInstant(), now)
                : Duration.ZERO;
    }
}
