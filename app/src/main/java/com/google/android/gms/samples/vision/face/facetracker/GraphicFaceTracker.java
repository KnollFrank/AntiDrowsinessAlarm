package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

class GraphicFaceTracker extends Tracker<Face> {

    private final EventBus eventBus;

    GraphicFaceTracker(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if(face.getIsLeftEyeOpenProbability() >= 0.5 && face.getIsRightEyeOpenProbability() >= 0.5) {
            this.eventBus.post(new EyesOpenedEvent(this.getTimestampMillis(detections)));
        } else if(face.getIsLeftEyeOpenProbability() < 0.5 && face.getIsRightEyeOpenProbability() < 0.5) {
            this.eventBus.post(new EyesClosedEvent(this.getTimestampMillis(detections)));
        }
    }

    private long getTimestampMillis(Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
