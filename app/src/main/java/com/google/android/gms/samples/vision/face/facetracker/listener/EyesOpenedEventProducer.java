package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

public class EyesOpenedEventProducer extends StateChangeEventProducer {

    public EyesOpenedEventProducer(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean hasState(Face face) {
        return this.isEyesOpen(face);
    }

    private boolean isEyesOpen(Face face) {
        return face.getIsLeftEyeOpenProbability() >= 0.5 && face.getIsRightEyeOpenProbability() >= 0.5;
    }

    @Override
    protected Object createStateEventFrom(UpdateEvent event) {
        return new EyesOpenedEvent(this.getTimestampMillis(event.getDetections()));
    }

    private long getTimestampMillis(Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
