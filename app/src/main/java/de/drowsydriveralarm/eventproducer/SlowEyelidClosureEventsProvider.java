package de.drowsydriveralarm.eventproducer;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import de.drowsydriveralarm.event.PendingSlowEyelidClosureEvent;
import de.drowsydriveralarm.event.SlowEyelidClosureEvent;

public class SlowEyelidClosureEventsProvider {

    private final List<SlowEyelidClosureEvent> events = new ArrayList<SlowEyelidClosureEvent>();
    private final Duration timeWindow;
    private PendingSlowEyelidClosureEvent pendingEvent;

    public SlowEyelidClosureEventsProvider(final Duration timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Subscribe
    public void recordSlowEyelidClosureEvent(final SlowEyelidClosureEvent event) {
        this.removePendingSlowEyelidClosureEvents();
        this.events.add(event);
        this.removeEventsNotPartlyWithinTimewindow(this.getEndOf(event));
    }

    private void removePendingSlowEyelidClosureEvents() {
        Iterables.removeIf(this.events, new Predicate<SlowEyelidClosureEvent>() {
            @Override
            public boolean apply(final SlowEyelidClosureEvent event) {
                return event instanceof PendingSlowEyelidClosureEvent;
            }
        });
    }

    private Instant getEndOf(final SlowEyelidClosureEvent event) {
        return event.getInstant().plus(event.getDuration());
    }

    private void removeEventsNotPartlyWithinTimewindow(final Instant now) {
        Iterables.removeIf(this.events, Predicates.not(this.isEventPartlyWithinTimeWindow(now)));
    }

    @VisibleForTesting
    List<SlowEyelidClosureEvent> getEvents() {
        return this.events;
    }

    public Duration getTimeWindow() {
        return this.timeWindow;
    }

    public List<SlowEyelidClosureEvent> getRecordedEventsPartlyWithinTimeWindow(final Instant timewindowEnd) {
        return FluentIterable
                .from(this.events)
                .filter(this.isEventPartlyWithinTimeWindow(timewindowEnd))
                .toList();
    }

    @NonNull
    private Predicate<SlowEyelidClosureEvent> isEventPartlyWithinTimeWindow(final Instant timewindowEnd) {
        final Interval timeWindowInterval = new Interval(this.timeWindow, timewindowEnd);
        return new Predicate<SlowEyelidClosureEvent>() {

            @Override
            public boolean apply(SlowEyelidClosureEvent event) {
                return timeWindowInterval.overlaps(event.getInterval());
            }
        };
    }
}
