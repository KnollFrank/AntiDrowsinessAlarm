package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.Optional;

public class ConsecutiveUpdateEvents {

    private final Optional<UpdateEvent> previousEvent;
    private final UpdateEvent actualEvent;

    public ConsecutiveUpdateEvents(final Optional<UpdateEvent> previousEvent, final UpdateEvent actualEvent) {
        this.previousEvent = previousEvent;
        this.actualEvent = actualEvent;
    }

    public Optional<UpdateEvent> getPreviousEvent() {
        return this.previousEvent;
    }

    public UpdateEvent getActualEvent() {
        return this.actualEvent;
    }
}