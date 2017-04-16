package de.drowsydriveralarm.event;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;

import org.joda.time.Instant;

public class EventHelper {

    public static Instant getInstantOf(final UpdateEvent event) {
        return getInstantOf(event.getDetections());
    }

    @NonNull
    public static Instant getInstantOf(final Detector.Detections<Face> detections) {
        return new Instant(detections.getFrameMetadata().getTimestampMillis());
    }
}
