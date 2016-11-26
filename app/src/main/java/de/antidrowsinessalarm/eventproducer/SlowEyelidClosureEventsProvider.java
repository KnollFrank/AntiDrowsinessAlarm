package de.antidrowsinessalarm.eventproducer;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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
    // TODO: this list will grow indefinitely when the app runs a long time, so shrink it somehow
    public void recordSlowEyelidClosureEvent(final SlowEyelidClosureEvent event) {
        this.events.add(event);
    }

    @VisibleForTesting
    List<SlowEyelidClosureEvent> getEvents() {
        return this.events;
    }

    public long getTimeWindowMillis() {
        return this.timeWindowMillis;
    }

    public List<SlowEyelidClosureEvent> getRecordedEventsPartlyWithinTimeWindow(final long nowMillis) {
        return FluentIterable
                .from(this.events)
                .filter(this.isEventPartlyWithinTimeWindow(nowMillis))
                .toList();
    }

    @NonNull
    private Predicate<SlowEyelidClosureEvent> isEventPartlyWithinTimeWindow(final long nowMillis) {
        // TODO: use Joda-Time Interval [startMillis, endMillis] or guava Range
        final long timewindowStartMillis = nowMillis - this.timeWindowMillis;
        final long timewindowEndMillis = nowMillis;
        return new Predicate<SlowEyelidClosureEvent>() {

            @Override
            public boolean apply(SlowEyelidClosureEvent event) {
                long eventEndMillis = event.getTimestampMillis() + event.getDurationMillis();
                return eventEndMillis >= timewindowStartMillis;
            }
        };
    }
}
