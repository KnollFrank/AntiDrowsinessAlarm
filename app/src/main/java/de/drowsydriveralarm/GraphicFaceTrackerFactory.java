package de.drowsydriveralarm;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import de.drowsydriveralarm.eventproducer.DefaultConfigFactory;
import de.drowsydriveralarm.eventproducer.DrowsyEventDetector;
import de.drowsydriveralarm.eventproducer.DrowsyEventDetectorConfig;

class GraphicFaceTrackerFactory {

    private static final String TAG = "FaceTrackerFactory";

    private final FaceTrackerActivity faceTrackerActivity;

    public GraphicFaceTrackerFactory(final FaceTrackerActivity faceTrackerActivity) {
        this.faceTrackerActivity = faceTrackerActivity;
    }

    @NonNull
    public Tracker<Face> createFaceTracker() {
        final DefaultConfigFactory configFactory = new DefaultConfigFactory(PreferenceManager.getDefaultSharedPreferences(this.faceTrackerActivity));
        DrowsyEventDetectorConfig drowsyEventDetectorConfig = DrowsyEventDetectorConfig
                .builder()
                .withEyeOpenProbabilityThreshold(configFactory.getEyeOpenProbabilityThreshold())
                .withConfig(configFactory.getConfig())
                .withSlowEyelidClosureMinDuration(configFactory.getSlowEyelidClosureMinDuration())
                .withTimeWindow(configFactory.getTimeWindow())
                .build();
        Log.i(TAG, "" + drowsyEventDetectorConfig);
        final DrowsyEventDetector drowsyEventDetector = new DrowsyEventDetector(drowsyEventDetectorConfig, true, new SystemClock());

        final Tracker<Face> tracker = new DisplayingGraphicFaceTracker(this.faceTrackerActivity);
        drowsyEventDetector.getEventBus().register(tracker);

        return new CompositeFaceTracker(drowsyEventDetector.getEventProducingGraphicFaceTracker(), tracker);
    }
}
