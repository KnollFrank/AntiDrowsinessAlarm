package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import de.antidrowsinessalarm.event.DurationEvent;
import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;

abstract class DurationEventProducer extends EventProducer {

    private Optional<Instant> eyesClosed = Optional.absent();

    DurationEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void recordEyesClosedEvent(final EyesClosedEvent eyesClosedEvent) {
        this.eyesClosed = Optional.of(eyesClosedEvent.getInstant());
    }

    @Subscribe
    public void recordEyesOpenedEventAndPostDurationEvent(final EyesOpenedEvent eyesOpenedEvent) {
        if (!this.eyesClosed.isPresent()) {
            return;
        }

        final Duration duration = new Duration(this.eyesClosed.get(), eyesOpenedEvent.getInstant());
        if (this.shallCreateEventFor(duration)) {
            this.postEvent(this.createDurationEvent(new Interval(this.eyesClosed.get(), duration)));
        }
    }

    protected abstract boolean shallCreateEventFor(final Duration duration);

    protected abstract DurationEvent createDurationEvent(final Interval interval);
}
