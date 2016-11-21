package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.ConsecutiveUpdateEvents;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ConsecutiveUpdateEventsProducer extends EventProducer {

    private UpdateEvent previousUpdateEvent = null;

    public ConsecutiveUpdateEventsProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void onUpdateEvent(final UpdateEvent actualUpdateEvent) {
        this.postEvent(new ConsecutiveUpdateEvents(this.previousUpdateEvent, actualUpdateEvent));
        this.previousUpdateEvent = actualUpdateEvent;
    }
}
