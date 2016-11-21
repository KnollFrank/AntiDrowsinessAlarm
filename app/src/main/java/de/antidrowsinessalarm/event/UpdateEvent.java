package de.antidrowsinessalarm.event;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.MoreObjects;

public class UpdateEvent extends Event {

    private final Detector.Detections<Face> detections;
    private final Face face;

    public UpdateEvent(Detector.Detections<Face> detections, Face face) {
        super(detections.getFrameMetadata().getTimestampMillis());
        this.detections = detections;
        this.face = face;
    }

    public Detector.Detections<Face> getDetections() {
        return this.detections;
    }

    public Face getFace() {
        return this.face;
    }

    @Override
    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super
                .getToStringHelper()
                .add("detections", this.detections)
                .add("face", this.face);
    }
}
