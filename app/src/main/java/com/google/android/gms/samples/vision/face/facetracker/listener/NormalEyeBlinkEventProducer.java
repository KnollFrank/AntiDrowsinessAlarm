package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.DurationEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.NormalEyeBlinkEvent;
import com.google.common.eventbus.EventBus;

public class NormalEyeBlinkEventProducer extends DurationEventProducer {

    public NormalEyeBlinkEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean shallCreateEventFor(long durationMillis) {
        return durationMillis < 500;
    }

    @Override
    protected DurationEvent createDurationEvent(long timestampMillis, long durationMillis) {
        return new NormalEyeBlinkEvent(timestampMillis, durationMillis);
    }
}
