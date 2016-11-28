package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.event.PendingSlowEyelidClosureEvent;
import de.antidrowsinessalarm.event.UpdateEvent;

public class PendingSlowEyelidClosureEventProducer extends EventProducer {

    private Optional<Long> eyesClosedMillis = Optional.absent();

    public PendingSlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void recordEyesClosedMillis(final EyesClosedEvent eyesClosedEvent) {
        this.eyesClosedMillis = Optional.of(eyesClosedEvent.getTimestampMillis());
    }

    @Subscribe
    public void removeEyesClosedMillis(final EyesOpenedEvent eyesOpenedEvent) {
        this.eyesClosedMillis = Optional.absent();
    }

    @Subscribe
    public void maybePostPendingSlowEyelidClosureEvent(final UpdateEvent updateEvent) {
        if(!this.eyesClosedMillis.isPresent()) {
            return;
        }

        final long durationMillis = updateEvent.getTimestampMillis() - this.eyesClosedMillis.get();
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        if(!EyesOpenedEventProducer.isEyesOpen(updateEvent.getFace()) && durationMillis >= 500) {
            this.postEvent(new PendingSlowEyelidClosureEvent(this.eyesClosedMillis.get(), durationMillis));
        }
    }
}
