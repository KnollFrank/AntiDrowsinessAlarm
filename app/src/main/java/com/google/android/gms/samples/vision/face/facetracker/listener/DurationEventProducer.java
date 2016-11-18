package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.DurationEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class DurationEventProducer extends EventProducer {

    private EyesClosedEvent eyesClosedEvent;

    DurationEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void recordEyesClosedEvent(final EyesClosedEvent eyesClosedEvent) {
        this.eyesClosedEvent = eyesClosedEvent;
    }

    @Subscribe
    public void recordEyesOpenedEventAndPostDurationEvent(final EyesOpenedEvent eyesOpenedEvent) {
        if(this.eyesClosedEvent == null) {
            return;
        }

        final long durationMillis = eyesOpenedEvent.getTimestampMillis() - this.eyesClosedEvent.getTimestampMillis();
        if(this.shallCreateEventFor(durationMillis)) {
            this.postEvent(this.createDurationEvent(this.eyesClosedEvent.getTimestampMillis(), durationMillis));
        }
    }

    protected abstract boolean shallCreateEventFor(final long durationMillis);

    protected abstract DurationEvent createDurationEvent(final long timestampMillis, final long durationMillis);
}
