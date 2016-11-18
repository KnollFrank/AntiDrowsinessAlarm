package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.DurationEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.common.eventbus.EventBus;

public class SlowEyelidClosureEventProducer extends DurationEventProducer {

    public SlowEyelidClosureEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean shallCreateEventFor(long duration) {
        return duration >= 500;
    }

    @Override
    protected DurationEvent createDurationEvent(long timestampMillis, long duration) {
        return new SlowEyelidClosureEvent(timestampMillis, duration);
    }
}
