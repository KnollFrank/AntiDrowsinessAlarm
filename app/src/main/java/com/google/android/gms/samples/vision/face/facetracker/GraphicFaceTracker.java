package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

class GraphicFaceTracker extends Tracker<Face> {

    private final EventBus eventBus;
    private final Boolean isEyesOpen = null;

    GraphicFaceTracker(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        this.eventBus.post(new UpdateEvent(detections, face));
    }
}
