package de.antidrowsinessalarm.eventproducer;

import android.support.annotation.NonNull;

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

    public long getTimeWindowMillis() {
        return this.timeWindowMillis;
    }

    public List<SlowEyelidClosureEvent> getRecordedEventsWithinTimeWindow(final long nowMillis) {
        return FluentIterable
                .from(this.events)
                .filter(this.isEventWithinTimeWindow(nowMillis))
                .toList();
    }

    @NonNull
    private Predicate<SlowEyelidClosureEvent> isEventWithinTimeWindow(final long nowMillis) {
        // TODO: use Joda-Time Interval [startMillis, endMillis]
        final long startMillis = nowMillis - this.timeWindowMillis;
        final long endMillis = nowMillis;
        return new Predicate<SlowEyelidClosureEvent>() {
            @Override
            public boolean apply(SlowEyelidClosureEvent event) {
                return event.getTimestampMillis() >= startMillis && event.getTimestampMillis() + event.getDurationMillis() <= endMillis;
            }
        };
    }
}
