package de.antidrowsinessalarm.eventproducer;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class SlowEyelidClosureEventsProvider {

    private final List<SlowEyelidClosureEvent> events = new ArrayList<SlowEyelidClosureEvent>();
    private final long timeWindowMillis;

    public SlowEyelidClosureEventsProvider(final long timeWindowMillis) {
        this.timeWindowMillis = timeWindowMillis;
    }

    @Subscribe
    public void recordSlowEyelidClosureEvent(final SlowEyelidClosureEvent event) {
        this.events.add(event);
        this.removeEventsNotPartlyWithinTimewindow(this.getEndMillis(event));
    }

    private long getEndMillis(final SlowEyelidClosureEvent event) {
        return event.getTimestampMillis() + event.getDurationMillis();
    }

    private void removeEventsNotPartlyWithinTimewindow(final long nowMillis) {
        Iterables.removeIf(this.events, Predicates.not(this.isEventPartlyWithinTimeWindow(nowMillis)));
    }

    @VisibleForTesting
    List<SlowEyelidClosureEvent> getEvents() {
        return this.events;
    }

    public long getTimeWindowMillis() {
        return this.timeWindowMillis;
    }

    public List<SlowEyelidClosureEvent> getRecordedEventsPartlyWithinTimeWindow(final long timewindowEndMillis) {
        return FluentIterable
                .from(this.events)
                .filter(this.isEventPartlyWithinTimeWindow(timewindowEndMillis))
                .toList();
    }

    @NonNull
    private Predicate<SlowEyelidClosureEvent> isEventPartlyWithinTimeWindow(final long timewindowEndMillis) {
        // TODO: use Joda-Time Interval [startMillis, endMillis] or guava Range
        final long timewindowStartMillis = timewindowEndMillis - this.timeWindowMillis;
        return new Predicate<SlowEyelidClosureEvent>() {

            @Override
            public boolean apply(SlowEyelidClosureEvent event) {
                return SlowEyelidClosureEventsProvider.this.getEndMillis(event) >= timewindowStartMillis;
            }
        };
    }
}
