package de.antidrowsinessalarm;

import android.util.Log;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import static android.content.ContentValues.TAG;

class CameraSourceHandler {

    private final FaceTrackerActivity faceTrackerActivity;
    private CameraSource cameraSource;

    public CameraSourceHandler(final FaceTrackerActivity faceTrackerActivity) {
        this.faceTrackerActivity = faceTrackerActivity;
    }

    public void createCameraSource() {
        final FaceDetector detector = this.createFaceDetector();
        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        this.cameraSource =
                new CameraSource.Builder(this.faceTrackerActivity.getApplicationContext(), detector)
                        .setRequestedPreviewSize(640, 480)
                        .setFacing(CameraSource.CAMERA_FACING_FRONT)
                        .setRequestedFps(30.0f)
                        .build();
    }

    private FaceDetector createFaceDetector() {
        return FaceDetectorFactory.createFaceDetector(
                this.faceTrackerActivity.getApplicationContext(),
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory(this.faceTrackerActivity)).build());
    }

    public CameraSource getCameraSource() {
        return this.cameraSource;
    }

    public void releaseCamera() {
        if (this.cameraSource != null) {
            this.cameraSource.release();
            this.cameraSource = null;
        }
    }
}
