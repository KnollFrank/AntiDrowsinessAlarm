package de.drowsydriveralarm.eventproducer;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;

public class ActiveAndIdleEventProducer extends Tracker<Face> {

    private final EventBus eventBus;

    public ActiveAndIdleEventProducer(final EventBus eventBus) {
        this.eventBus  = eventBus;
    }

    @Override
    public void onNewItem(final int i, final Face face) {
        this.eventBus.post(new AppActiveEvent(null));
    }

    @Override
    public void onUpdate(final Detector.Detections<Face> detections, final Face face) {
        super.onUpdate(detections, face);
    }

    @Override
    public void onMissing(final Detector.Detections<Face> detections) {
        this.eventBus.post(new AppIdleEvent(null));
    }

    @Override
    public void onDone() {
        this.eventBus.post(new AppIdleEvent(null));
    }
}
