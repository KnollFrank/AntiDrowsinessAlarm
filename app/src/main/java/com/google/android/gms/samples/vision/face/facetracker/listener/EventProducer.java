package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.NormalEyeBlinkEvent;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class EventProducer {

    private final EventBus eventBus;
    private EyesClosedEvent eyesClosedEvent;

    public EventProducer(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void recordEyesClosedEvent(EyesClosedEvent eyesClosedEvent) {
        this.eyesClosedEvent = eyesClosedEvent;
    }

    @Subscribe
    public void recordEyesOpenedEventAndPostEvent(EyesOpenedEvent eyesOpenedEvent) {
        if (eyesClosedEvent == null) {
            return;
        }

        long duration = eyesOpenedEvent.getTimestampMillis() - eyesClosedEvent.getTimestampMillis();
        if (shallCreateEventFor(duration)) {
            eventBus.post(createEvent(eyesClosedEvent.getTimestampMillis(), duration));
        }
    }

    protected abstract boolean shallCreateEventFor(long duration);

    protected abstract Event createEvent(final long timestampMillis, final long duration);
}
