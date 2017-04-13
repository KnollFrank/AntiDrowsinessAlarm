package de.drowsydriveralarm;

import android.content.Context;

import com.google.android.gms.vision.face.FaceDetector;

public class FaceDetectorFactory {

    public static FaceDetector createFaceDetector(final Context context) {
        return new FaceDetector
                .Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(false) // true hat im Test nicht funktioniert, wo Einzelbilder statt Videos verwendet werden
                .setProminentFaceOnly(true)
                .build();
    }
}
