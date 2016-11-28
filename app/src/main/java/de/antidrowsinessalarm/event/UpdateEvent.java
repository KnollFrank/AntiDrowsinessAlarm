package de.antidrowsinessalarm.event;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.MoreObjects;

import org.joda.time.Instant;

public class UpdateEvent extends Event {

    private final Detector.Detections<Face> detections;
    private final Face face;

    public UpdateEvent(Detector.Detections<Face> detections, Face face) {
        super(new Instant(detections.getFrameMetadata().getTimestampMillis()));
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
                .add("detections", this.toString(this.detections))
                .add("face", this.toString(this.face));
    }

    private String toString(final Detector.Detections<Face> detections) {
        return MoreObjects
                .toStringHelper(detections)
                .add("timestampMillis", detections.getFrameMetadata().getTimestampMillis())
                .toString();
    }

    private String toString(final Face face) {
        return MoreObjects
                .toStringHelper(face)
                .add("leftEyeOpenProbability", face.getIsLeftEyeOpenProbability())
                .add("rightEyeOpenProbability", face.getIsRightEyeOpenProbability())
                .toString();
    }
}
