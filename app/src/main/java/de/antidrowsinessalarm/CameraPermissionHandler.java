package de.antidrowsinessalarm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

class CameraPermissionHandler {

    private static final String TAG = "CameraPermissionHandler";

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private final FaceTrackerActivity faceTrackerActivity;
    private final CameraSourceHandler cameraSourceHandler;

    public CameraPermissionHandler(final FaceTrackerActivity faceTrackerActivity, final CameraSourceHandler cameraSourceHandler) {
        this.faceTrackerActivity = faceTrackerActivity;
        this.cameraSourceHandler = cameraSourceHandler;
    }

    public void createCameraSourceOrRequestCameraPermission() {
        final int rc = ActivityCompat.checkSelfPermission(this.faceTrackerActivity, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            this.cameraSourceHandler.createCameraSource();
        } else {
            this.requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this.faceTrackerActivity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this.faceTrackerActivity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                ActivityCompat.requestPermissions(CameraPermissionHandler.this.faceTrackerActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(this.faceTrackerActivity.getmGraphicOverlay(), R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            this.faceTrackerActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            this.cameraSourceHandler.createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                CameraPermissionHandler.this.faceTrackerActivity.finish();
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.faceTrackerActivity);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }
}
