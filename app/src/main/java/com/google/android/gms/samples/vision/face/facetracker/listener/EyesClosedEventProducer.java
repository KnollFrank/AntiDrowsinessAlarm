package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

public class EyesClosedEventProducer extends StateChangeEventProducer {

    public EyesClosedEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean getState(final Face face) {
        return this.isEyesClosed(face);
    }

    private boolean isEyesClosed(final Face face) {
        return face.getIsLeftEyeOpenProbability() < 0.5 && face.getIsRightEyeOpenProbability() < 0.5;
    }

    @Override
    protected Object createStateChangeEventFrom(final UpdateEvent event) {
        return new EyesClosedEvent(this.getTimestampMillis(event.getDetections()));
    }

    private long getTimestampMillis(final Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
