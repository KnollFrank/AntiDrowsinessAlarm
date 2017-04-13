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
    public void onNewItem(int i, Face face) {
        this.eventBus.post(new AppActiveEvent(null));
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        super.onUpdate(detections, face);
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        this.eventBus.post(new AppIdleEvent(null));
    }

    @Override
    public void onDone() {
        super.onDone();
    }
}
