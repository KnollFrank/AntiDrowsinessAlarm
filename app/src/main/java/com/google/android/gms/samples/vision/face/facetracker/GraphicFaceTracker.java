package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

public class GraphicFaceTracker extends Tracker<Face> {

    private final EventBus eventBus;

    public GraphicFaceTracker(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if(face.getIsLeftEyeOpenProbability() >= 0.5 && face.getIsRightEyeOpenProbability() >= 0.5) {
            this.eventBus.post(Event.EyesOpenedEvent);
        } else if(face.getIsLeftEyeOpenProbability() < 0.5 && face.getIsRightEyeOpenProbability() < 0.5) {
            this.eventBus.post(Event.EyesClosedEvent);
        }
    }
}
