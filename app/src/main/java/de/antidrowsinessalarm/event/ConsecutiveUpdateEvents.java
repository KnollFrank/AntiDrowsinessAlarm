package de.antidrowsinessalarm.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import org.joda.time.Instant;

public class ConsecutiveUpdateEvents extends Event {

    private final Optional<UpdateEvent> previousEvent;
    private final UpdateEvent actualEvent;

    public ConsecutiveUpdateEvents(final Optional<UpdateEvent> previousEvent, final UpdateEvent actualEvent) {
        super(new Instant(actualEvent.getDetections().getFrameMetadata().getTimestampMillis()));
        this.previousEvent = previousEvent;
        this.actualEvent = actualEvent;
    }

    public Optional<UpdateEvent> getPreviousEvent() {
        return this.previousEvent;
    }

    public UpdateEvent getActualEvent() {
        return this.actualEvent;
    }

    @Override
    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super
                .getToStringHelper()
                .add("previousEvent", this.previousEvent)
                .add("actualEvent", this.actualEvent);
    }
}