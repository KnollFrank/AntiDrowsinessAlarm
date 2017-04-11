package de.antidrowsinessalarm;

import android.app.Dialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

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
        final FaceDetector detector = FaceDetectorFactory.createFaceDetector(this.faceTrackerActivity.getApplicationContext());
        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        final LargestFaceFocusingProcessor processor =
                new LargestFaceFocusingProcessor.Builder(
                        detector,
                        new GraphicFaceTrackerFactory(this.faceTrackerActivity).createFaceTracker())
                .build();
        detector.setProcessor(processor);

        this.cameraSource =
                new CameraSource.Builder(this.faceTrackerActivity.getApplicationContext(), detector)
                        .setRequestedPreviewSize(640, 480)
                        .setFacing(CameraSource.CAMERA_FACING_FRONT)
                        .setRequestedFps(30.0f)
                        .build();
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
            final Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this.faceTrackerActivity, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (this.getCameraSource() != null) {
            try {
                this.faceTrackerActivity.getPreview().start(this.getCameraSource(), this.faceTrackerActivity.getGraphicOverlay());
            } catch (final IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                this.releaseCameraSource();
            }
        }
    }
}
