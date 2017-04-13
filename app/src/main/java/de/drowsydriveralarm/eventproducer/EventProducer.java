package de.drowsydriveralarm.eventproducer;

import com.google.common.eventbus.EventBus;

abstract class EventProducer {

    private final EventBus eventBus;

    EventProducer(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected void postEvent(Object event) {
        this.eventBus.post(event);
    }
}
