package de.drowsydriveralarm.event;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.MoreObjects;

public class UpdateEvent extends Event {

    private final Detector.Detections<Face> detections;
    private final Face face;

    public UpdateEvent(final Detector.Detections<Face> detections, final Face face) {
        super(EventHelper.getInstantOf(detections));
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
                .add("timestampMillis", EventHelper.getInstantOf(detections).getMillis())
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
