package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

abstract class StateChangeEventProducer extends EventProducer {

    private boolean hasState = false;

    StateChangeEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void maybeProduceStateChangeEvent(final UpdateEvent event) {
        if(!this.hasState) {
            if(this.hasState(event.getFace())) {
                this.postEvent(this.createStateEventFrom(event));
                this.hasState = true;
            }
        } else {
            this.hasState = this.hasState(event.getFace());
        }
    }

    protected abstract boolean hasState(Face face);

    protected abstract Object createStateEventFrom(UpdateEvent event);
}
