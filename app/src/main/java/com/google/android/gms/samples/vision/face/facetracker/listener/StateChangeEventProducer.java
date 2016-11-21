package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.ConsecutiveUpdateEvents;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class StateChangeEventProducer extends EventProducer {

    StateChangeEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void maybeProduceStateChangeEvent(final ConsecutiveUpdateEvents events) {
        this.maybeProduceStateChangeEvent(events.getPreviousUpdateEvent(), events.getActualUpdateEvent());
    }

    public void maybeProduceStateChangeEvent(final UpdateEvent previousEvent, final UpdateEvent actualEvent) {
        if(!this.hasPreviousState(previousEvent) && this.hasActualState(actualEvent)) {
            this.postEvent(this.createStateChangeEventFrom(actualEvent));
        }
    }

    private boolean hasPreviousState(final UpdateEvent event) {
        return event != null && this.getState(event.getFace());
    }

    private boolean hasActualState(final UpdateEvent event) {
        return this.getState(event.getFace());
    }

    protected abstract boolean getState(Face face);

    protected abstract Object createStateChangeEventFrom(UpdateEvent event);
}
