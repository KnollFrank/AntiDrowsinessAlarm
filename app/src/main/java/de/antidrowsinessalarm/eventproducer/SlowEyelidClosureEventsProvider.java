package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class SlowEyelidClosureEventsProvider {

    private final List<SlowEyelidClosureEvent> events = new ArrayList<SlowEyelidClosureEvent>();

    @Subscribe
    // TODO: this list will grow indefinitely when the app runs a long time, so shrink it somehow
    public void recordSlowEyelidClosureEvent(final SlowEyelidClosureEvent event) {
        this.events.add(event);
    }

    public List<SlowEyelidClosureEvent> getRecordedEventsWithinTimeWindow(final long nowMillis, final long timeWindowMillis) {
        // TODO: use Joda-Time Interval [startMillis, endMillis]
        final long startMillis = nowMillis - timeWindowMillis;
        final long endMillis = nowMillis;
        Predicate<SlowEyelidClosureEvent> isEventWithinTimeWindow =
                new Predicate<SlowEyelidClosureEvent>() {
                    @Override
                    public boolean apply(SlowEyelidClosureEvent event) {
                        return event.getTimestampMillis() >= startMillis && event.getTimestampMillis() + event.getDurationMillis() <= endMillis;
                    }
                };
        return FluentIterable.from(this.events).filter(isEventWithinTimeWindow).toList();
    }
}
