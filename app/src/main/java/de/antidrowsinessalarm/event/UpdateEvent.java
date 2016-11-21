package de.antidrowsinessalarm.event;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;

public class UpdateEvent {

    private final Detector.Detections<Face> detections;
    private final Face face;

    public UpdateEvent(Detector.Detections<Face> detections, Face face) {
        this.detections = detections;
        this.face = face;
    }

    public Detector.Detections<Face> getDetections() {
        return this.detections;
    }

    public Face getFace() {
        return this.face;
    }
}
