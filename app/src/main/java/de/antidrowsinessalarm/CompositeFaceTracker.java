package de.antidrowsinessalarm;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

class CompositeFaceTracker extends Tracker<Face> {

    private final Tracker<Face> tracker1;
    private final Tracker<Face> tracker2;

    public CompositeFaceTracker(Tracker<Face> tracker1, Tracker<Face> tracker2) {
        this.tracker1 = tracker1;
        this.tracker2 = tracker2;
    }

    @Override
    public void onNewItem(int faceId, Face face) {
        this.tracker1.onNewItem(faceId, face);
        this.tracker2.onNewItem(faceId, face);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        this.tracker1.onUpdate(detections, face);
        this.tracker2.onUpdate(detections, face);
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        this.tracker1.onMissing(detections);
        this.tracker2.onMissing(detections);
    }

    @Override
    public void onDone() {
        this.tracker1.onDone();
        this.tracker2.onDone();
    }
}
