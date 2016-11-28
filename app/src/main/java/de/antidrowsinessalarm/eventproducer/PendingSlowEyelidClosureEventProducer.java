package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.PendingSlowEyelidClosureEvent;
import de.antidrowsinessalarm.event.UpdateEvent;

public class PendingSlowEyelidClosureEventProducer extends EventProducer {

    private EyesClosedEvent eyesClosedEvent;

    public PendingSlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void recordEyesClosedEvent(final EyesClosedEvent eyesClosedEvent) {
        this.eyesClosedEvent = eyesClosedEvent;
    }

    @Subscribe
    public void recordEyesOpenedEventAndPostDurationEvent(final UpdateEvent updateEvent) {
        if(this.eyesClosedEvent == null) {
            return;
        }

        final long durationMillis = updateEvent.getTimestampMillis() - this.eyesClosedEvent.getTimestampMillis();
        if(!EyesOpenedEventProducer.isEyesOpen(updateEvent.getFace()) && this.shallCreateEventFor(durationMillis)) {
            this.postEvent(this.createDurationEvent(this.eyesClosedEvent.getTimestampMillis(), durationMillis));
        }
    }

    private boolean shallCreateEventFor(long durationMillis) {
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        return durationMillis >= 500;
    }

    private DurationEvent createDurationEvent(long timestampMillis, long durationMillis) {
        return new PendingSlowEyelidClosureEvent(timestampMillis, durationMillis);
    }
}
