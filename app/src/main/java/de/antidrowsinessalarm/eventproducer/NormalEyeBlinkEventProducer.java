package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.NormalEyeBlinkEvent;

public class NormalEyeBlinkEventProducer extends DurationEventProducer {

    public NormalEyeBlinkEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean shallCreateEventFor(long durationMillis) {
        return durationMillis < 500;
    }

    @Override
    protected DurationEvent createDurationEvent(long timestampMillis, long durationMillis) {
        return new NormalEyeBlinkEvent(timestampMillis, durationMillis);
    }
}
