package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Instant;

import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.event.PendingSlowEyelidClosureEvent;
import de.antidrowsinessalarm.event.UpdateEvent;

public class PendingSlowEyelidClosureEventProducer extends EventProducer {

    private Optional<Instant> eyesClosed = Optional.absent();

    public PendingSlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void recordEyesClosed(final EyesClosedEvent eyesClosedEvent) {
        this.eyesClosed = Optional.of(eyesClosedEvent.getInstant());
    }

    @Subscribe
    public void removeEyesClosed(final EyesOpenedEvent eyesOpenedEvent) {
        this.eyesClosed = Optional.absent();
    }

    @Subscribe
    public void maybePostPendingSlowEyelidClosureEvent(final UpdateEvent updateEvent) {
        if(!this.eyesClosed.isPresent()) {
            return;
        }

        final Duration duration = new Duration(this.eyesClosed.get(), updateEvent.getInstant());
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        if(!EyesOpenedEventProducer.isEyesOpen(updateEvent.getFace()) && SlowEyelidClosureEventProducer.isGreaterOrEqualThanSlowEyelidClosureMinDuration(duration)) {
            this.postEvent(new PendingSlowEyelidClosureEvent(this.eyesClosed.get(), duration));
        }
    }
}
