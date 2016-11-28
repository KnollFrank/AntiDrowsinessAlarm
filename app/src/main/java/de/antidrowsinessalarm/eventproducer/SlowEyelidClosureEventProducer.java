package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;
import org.joda.time.Interval;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class SlowEyelidClosureEventProducer extends DurationEventProducer {

    private final Duration slowEyelidClosureMinDuration;

    public SlowEyelidClosureEventProducer(final Duration slowEyelidClosureMinDuration, final EventBus eventBus) {
        super(eventBus);
        this.slowEyelidClosureMinDuration = slowEyelidClosureMinDuration;
    }

    static boolean isSlowEyelidClosure(final Duration duration, final Duration slowEyelidClosureMinDuration) {
        return duration.isLongerThan(slowEyelidClosureMinDuration) || duration.isEqual(slowEyelidClosureMinDuration);
    }

    @Override
    protected boolean shallCreateEventFor(Duration duration) {
        return isSlowEyelidClosure(duration, this.slowEyelidClosureMinDuration);
    }

    @Override
    protected DurationEvent createDurationEvent(final Interval interval) {
        return new SlowEyelidClosureEvent(interval.getStart().toInstant(), interval.toDuration());
    }
}
