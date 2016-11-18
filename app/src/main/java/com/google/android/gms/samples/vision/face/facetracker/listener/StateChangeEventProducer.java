package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class StateChangeEventProducer extends EventProducer {

    private boolean hasPreviousState = false;

    StateChangeEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void maybeProduceStateChangeEvent(final UpdateEvent actualEvent) {
        boolean hasActualState = this.getState(actualEvent.getFace());
        if(!this.hasPreviousState && hasActualState) {
            this.postEvent(this.createStateChangeEventFrom(actualEvent));
        }

        this.hasPreviousState = hasActualState;
    }

    protected abstract boolean getState(Face face);

    protected abstract Object createStateChangeEventFrom(UpdateEvent event);
}
