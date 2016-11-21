package de.antidrowsinessalarm.listener;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.NormalEyeBlinkEvent;
import com.google.common.eventbus.EventBus;

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
