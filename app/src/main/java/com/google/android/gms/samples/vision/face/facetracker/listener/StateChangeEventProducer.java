package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.ConsecutiveUpdateEvents;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class StateChangeEventProducer extends EventProducer {

    StateChangeEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void maybeProduceStateChangeEvent(final ConsecutiveUpdateEvents events) {
        this.maybeProduceStateChangeEvent(events.getPreviousEvent(), events.getActualEvent());
    }

    public void maybeProduceStateChangeEvent(final Optional<UpdateEvent> previousEvent, final UpdateEvent actualEvent) {
        if(!this.hasPreviousState(previousEvent) && this.hasActualState(actualEvent)) {
            this.postEvent(this.createStateChangeEventFrom(actualEvent));
        }
    }

    private boolean hasPreviousState(final Optional<UpdateEvent> event) {
        return event.isPresent() && this.getState(event.get().getFace());
    }

    private boolean hasActualState(final UpdateEvent event) {
        return this.getState(event.getFace());
    }

    protected abstract boolean getState(Face face);

    protected abstract Object createStateChangeEventFrom(UpdateEvent event);
}
