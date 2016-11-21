package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.ConsecutiveUpdateEvents;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ConsecutiveUpdateEventsProducer extends EventProducer {

    private Optional<UpdateEvent> previousEvent = Optional.absent();

    public ConsecutiveUpdateEventsProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void onUpdateEvent(final UpdateEvent actualEvent) {
        this.postEvent(new ConsecutiveUpdateEvents(this.previousEvent, actualEvent));
        this.previousEvent = Optional.of(actualEvent);
    }
}
