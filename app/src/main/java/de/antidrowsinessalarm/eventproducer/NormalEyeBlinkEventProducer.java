package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;
import org.joda.time.Interval;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.NormalEyeBlinkEvent;

public class NormalEyeBlinkEventProducer extends DurationEventProducer {

    public NormalEyeBlinkEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean shallCreateEventFor(Duration duration) {
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        return duration.isShorterThan(new Duration(500));
    }

    @Override
    protected DurationEvent createDurationEvent(final Interval interval) {
        return new NormalEyeBlinkEvent(interval.getStart().toInstant(), interval.toDuration());
    }
}
