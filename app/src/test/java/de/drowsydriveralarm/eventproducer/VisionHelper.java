package de.drowsydriveralarm.eventproducer;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import org.joda.time.Instant;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;

public class VisionHelper {

    static Detector.Detections<Face> getFaceDetections(final Instant instant) {
        final Frame.Metadata metaData = Mockito.mock(Frame.Metadata.class);
        doReturn(instant.getMillis()).when(metaData).getTimestampMillis();

        final Detector.Detections<Face> detections = Mockito.mock(Detector.Detections.class);
        doReturn(metaData).when(detections).getFrameMetadata();

        return detections;
    }

    static Face createFaceWithEyesClosed() {
        return createFace(0.4f, 0.4f);
    }

    static Face createFace(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability) {
        final Face face = createFaceWithLandmarks(Arrays.asList(createLandmark(Landmark.LEFT_EYE), createLandmark(Landmark.RIGHT_EYE)));
        doReturn(isLeftEyeOpenProbability).when(face).getIsLeftEyeOpenProbability();
        doReturn(isRightEyeOpenProbability).when(face).getIsRightEyeOpenProbability();
        return face;
    }

    static Face createFaceWithEyesOpened() {
        return createFace(0.8f, 0.8f);
    }

    static Face createFaceWithLandmarks(final List<Landmark> landmarks) {
        final Face face = Mockito.mock(Face.class);
        doReturn(landmarks).when(face).getLandmarks();
        return face;
    }

    @NonNull
    static Landmark createLandmark(final int type) {
        return new Landmark(new PointF(1, 1), type);
    }
}
