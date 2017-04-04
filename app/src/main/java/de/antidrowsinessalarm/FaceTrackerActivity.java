/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.antidrowsinessalarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import de.antidrowsinessalarm.camera.CameraSourcePreview;
import de.antidrowsinessalarm.camera.GraphicOverlay;

/**
 * Activity for the face tracker app. This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
// TODO: brauchen Ampel, die anzeigt, wie lange die Anwendung bereits unbrauchbar ist, also z.B. das Gesicht des Fahrers oder seine Augen nicht verfolgen bzw. erkennen konnte und infolgedessen keinen möglicherweise notwendigen Schläfrigkeitsalarm auslösen konnte.
// TOOO: die Anwendung soll sich in den Hintegrund schalten können, und sich im Falle von erkannter Schläfrigkeit des Fahrers in den Vordergrund schalten können, bzw. lediglich einen Alarmton abgeben und ein rotes Signal über der gerade aktiven Anwendung einblenden.
public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "CompositeFaceTracker";
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private TextView eyesInfoView;
    private ImageView imageView;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    public static FaceDetector createFaceDetector(final Context context, final Detector.Processor<Face> processor) {
        final FaceDetector detector =
                new FaceDetector
                        .Builder(context)
                        .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                        .setMode(FaceDetector.ACCURATE_MODE)
                        .setTrackingEnabled(false) // true hat im Test nicht funktioniert, wo Einzelbilder statt Videos verwendet werden
                        .setProminentFaceOnly(true)
                        .build();
        detector.setProcessor(processor);
        return detector;
    }

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.main);
        final Toolbar myToolbar = (Toolbar) this.findViewById(R.id.my_toolbar);
        this.setSupportActionBar(myToolbar);

        this.mPreview = (CameraSourcePreview) this.findViewById(R.id.preview);
        this.mGraphicOverlay = (GraphicOverlay) this.findViewById(R.id.faceOverlay);
        this.eyesInfoView = (TextView) this.findViewById(R.id.eyesInfoView);
        this.imageView = (ImageView) this.findViewById(R.id.imageView);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        final int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            this.createCameraSource();
        } else {
            this.requestCameraPermission();
        }
    }

    public GraphicOverlay getmGraphicOverlay() {
        return this.mGraphicOverlay;
    }

    public TextView getEyesInfoView() {
        return this.eyesInfoView;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(this.mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {
        final FaceDetector detector =
                createFaceDetector(
                        this.getApplicationContext(),
                        new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory(this)).build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        this.mCameraSource = new CameraSource.Builder(this.getApplicationContext(), detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (this.mCameraSource != null) {
            this.mCameraSource.release();
        }
        this.createCameraSource();
        this.startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCameraSource != null) {
            this.mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            this.createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                FaceTrackerActivity.this.finish();
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        final int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                this.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            final Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (this.mCameraSource != null) {
            try {
                this.mPreview.start(this.mCameraSource, this.mGraphicOverlay);
            } catch (final IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                this.mCameraSource.release();
                this.mCameraSource = null;
            }
        }
    }
}
