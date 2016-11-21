package de.antidrowsinessalarm.listener;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;
import com.google.common.eventbus.EventBus;

public class SlowEyelidClosureEventProducer extends DurationEventProducer {

    public SlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean shallCreateEventFor(long durationMillis) {
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        return durationMillis >= 500;
    }

    @Override
    protected DurationEvent createDurationEvent(long timestampMillis, long durationMillis) {
        return new SlowEyelidClosureEvent(timestampMillis, durationMillis);
    }
}
