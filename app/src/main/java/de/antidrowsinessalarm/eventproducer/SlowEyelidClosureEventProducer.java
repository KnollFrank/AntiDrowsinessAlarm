package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;
import org.joda.time.Interval;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class SlowEyelidClosureEventProducer extends DurationEventProducer {

    public SlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    static boolean isGreaterOrEqualThanSlowEyelidClosureMinDuration(final Duration duration) {
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        Duration slowEyelidClosureMinDuration = new Duration(500);
        return duration.isLongerThan(slowEyelidClosureMinDuration) || duration.isEqual(slowEyelidClosureMinDuration);
    }

    @Override
    protected boolean shallCreateEventFor(Duration duration) {
        return isGreaterOrEqualThanSlowEyelidClosureMinDuration(duration);

    }

    @Override
    protected DurationEvent createDurationEvent(final Interval interval) {
        return new SlowEyelidClosureEvent(interval.getStart().toInstant(), interval.toDuration());
    }
}
