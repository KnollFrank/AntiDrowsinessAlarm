package de.antidrowsinessalarm;

import android.app.Dialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import static android.content.ContentValues.TAG;

class CameraSourceHandler {

    private static final int RC_HANDLE_GMS = 9001;

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

    public void releaseCameraSource() {
        if (this.cameraSource != null) {
            this.cameraSource.release();
            this.cameraSource = null;
        }
    }

    public void startCameraSource() {
        final int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.faceTrackerActivity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            final Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(faceTrackerActivity, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (this.getCameraSource() != null) {
            try {
                this.faceTrackerActivity.getmPreview().start(this.getCameraSource(), this.faceTrackerActivity.getmGraphicOverlay());
            } catch (final IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                this.releaseCameraSource();
            }
        }
    }
}
