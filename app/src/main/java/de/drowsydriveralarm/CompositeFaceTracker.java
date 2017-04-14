package de.drowsydriveralarm;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class CompositeFaceTracker extends Tracker<Face> {

    private final Tracker<Face> tracker1;
    private final Tracker<Face> tracker2;

    public CompositeFaceTracker(final Tracker<Face> tracker1, final Tracker<Face> tracker2) {
        this.tracker1 = tracker1;
        this.tracker2 = tracker2;
    }

    @Override
    public void onNewItem(final int faceId, final Face face) {
        this.tracker1.onNewItem(faceId, face);
        this.tracker2.onNewItem(faceId, face);
    }

    @Override
    public void onUpdate(final Detector.Detections<Face> detections, final Face face) {
        this.tracker1.onUpdate(detections, face);
        this.tracker2.onUpdate(detections, face);
    }

    @Override
    public void onMissing(final Detector.Detections<Face> detections) {
        this.tracker1.onMissing(detections);
        this.tracker2.onMissing(detections);
    }

    @Override
    public void onDone() {
        this.tracker1.onDone();
        this.tracker2.onDone();
    }
}
