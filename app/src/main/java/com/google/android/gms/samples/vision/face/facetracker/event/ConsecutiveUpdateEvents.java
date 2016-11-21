package com.google.android.gms.samples.vision.face.facetracker.event;

public class ConsecutiveUpdateEvents {

    private final UpdateEvent previousEvent;
    private final UpdateEvent actualEvent;

    public ConsecutiveUpdateEvents(final UpdateEvent previousEvent, final UpdateEvent actualEvent) {
        this.previousEvent = previousEvent;
        this.actualEvent = actualEvent;
    }

    public UpdateEvent getPreviousEvent() {
        return this.previousEvent;
    }

    public UpdateEvent getActualEvent() {
        return this.actualEvent;
    }
}