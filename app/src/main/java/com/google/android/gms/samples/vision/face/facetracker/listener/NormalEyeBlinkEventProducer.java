package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.NormalEyeBlinkEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NormalEyeBlinkEventProducer {

    private final EventBus eventBus;
    private EyesClosedEvent eyesClosedEvent;

    public NormalEyeBlinkEventProducer(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void recordEyesClosedEvent(EyesClosedEvent eyesClosedEvent) {
        this.eyesClosedEvent = eyesClosedEvent;
    }

    @Subscribe
    public void recordEyesOpenedEvent(EyesOpenedEvent eyesOpenedEvent) {
        long duration = eyesOpenedEvent.getTimestampMillis() - eyesClosedEvent.getTimestampMillis();
        if(duration < 500) {
            eventBus.post(new NormalEyeBlinkEvent(eyesClosedEvent.getTimestampMillis(), duration));
        } else {
            eventBus.post(new SlowEyelidClosureEvent(eyesClosedEvent.getTimestampMillis(), duration));
        }
    }
}
