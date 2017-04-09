package de.antidrowsinessalarm;

import android.media.MediaPlayer;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.AwakeEvent;
import de.antidrowsinessalarm.event.DrowsyEvent;
import de.antidrowsinessalarm.event.LikelyDrowsyEvent;

/**
 * Face tracker for each detected individual. This maintains a face graphic within the app's
 * associated face overlay.
 */
class DisplayingGraphicFaceTracker extends Tracker<Face> {

    private static final String TAG = "FaceTracker";

    private final FaceGraphic faceGraphic;
    private final MediaPlayer mediaPlayer;
    private final FaceTrackerActivity faceTrackerActivity;

    DisplayingGraphicFaceTracker(final FaceTrackerActivity faceTrackerActivity) {
        this.faceGraphic = new FaceGraphic(faceTrackerActivity.getGraphicOverlay());
        this.faceTrackerActivity = faceTrackerActivity;
        this.mediaPlayer = MediaPlayer.create(faceTrackerActivity.getApplicationContext(), R.raw.hupe);
    }

    @Subscribe
    public void onDrowsyEvent(final DrowsyEvent event) {
        this.faceTrackerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                faceTrackerActivity.getEyesInfoView().setText("" + event);
                faceTrackerActivity.getImageView().setImageResource(R.drawable.red);
                DisplayingGraphicFaceTracker.this.mediaPlayer.start();
            }
        });
    }

    @Subscribe
    public void onLikelyDrowsyEvent(final LikelyDrowsyEvent event) {
        this.faceTrackerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                faceTrackerActivity.getEyesInfoView().setText("" + event);
                faceTrackerActivity.getImageView().setImageResource(R.drawable.yellow);
            }
        });
    }

    @Subscribe
    public void onAwakeEvent(final AwakeEvent event) {
        this.faceTrackerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                faceTrackerActivity.getEyesInfoView().setText("" + event);
                faceTrackerActivity.getImageView().setImageResource(R.drawable.green);
            }
        });
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem(final int faceId, final Face item) {
        Log.i(TAG, "onNewItem called");
        this.faceGraphic.setId(faceId);
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(final FaceDetector.Detections<Face> detectionResults, final Face face) {
        Log.i(TAG, "onUpdate called");
        this.faceTrackerActivity.getGraphicOverlay().add(this.faceGraphic);
        this.faceGraphic.updateFace(face);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(final FaceDetector.Detections<Face> detectionResults) {
        this.faceTrackerActivity.getGraphicOverlay().remove(this.faceGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        this.faceTrackerActivity.getGraphicOverlay().remove(this.faceGraphic);
        this.mediaPlayer.release();
    }
}
