package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.DurationEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class DurationEventProducer {

    private final EventBus eventBus;
    private EyesClosedEvent eyesClosedEvent;

    DurationEventProducer(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void recordEyesClosedEvent(EyesClosedEvent eyesClosedEvent) {
        this.eyesClosedEvent = eyesClosedEvent;
    }

    @Subscribe
    public void recordEyesOpenedEventAndPostDurationEvent(EyesOpenedEvent eyesOpenedEvent) {
        if(this.eyesClosedEvent == null) {
            return;
        }

        long duration=eyesOpenedEvent.getTimestampMillis() - this.eyesClosedEvent.getTimestampMillis();
        if(this.shallCreateEventFor(duration)) {
            this.eventBus.post(this.createDurationEvent(this.eyesClosedEvent.getTimestampMillis(), duration));
        }
    }

    protected abstract boolean shallCreateEventFor(long duration);

    protected abstract DurationEvent createDurationEvent(final long timestampMillis, final long duration);
}
