package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.DurationEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.common.eventbus.EventBus;

public class SlowEyelidClosureEventProducer extends DurationEventProducer {

    public SlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean shallCreateEventFor(long durationMillis) {
        // TODO: make durationMillis configurable from 300 to 500 milliseconds
        return durationMillis >= 500;
    }

    @Override
    protected DurationEvent createDurationEvent(long timestampMillis, long durationMillis) {
        return new SlowEyelidClosureEvent(timestampMillis, durationMillis);
    }
}
