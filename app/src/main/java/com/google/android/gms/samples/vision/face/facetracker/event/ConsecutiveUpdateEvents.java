package com.google.android.gms.samples.vision.face.facetracker.event;

public class ConsecutiveUpdateEvents {

    private final UpdateEvent previousUpdateEvent;
    private final UpdateEvent actualUpdateEvent;

    public ConsecutiveUpdateEvents(final UpdateEvent previousUpdateEvent, final UpdateEvent actualUpdateEvent) {
        this.previousUpdateEvent = previousUpdateEvent;
        this.actualUpdateEvent = actualUpdateEvent;
    }

    public UpdateEvent getPreviousUpdateEvent() {
        return this.previousUpdateEvent;
    }

    public UpdateEvent getActualUpdateEvent() {
        return this.actualUpdateEvent;
    }
}