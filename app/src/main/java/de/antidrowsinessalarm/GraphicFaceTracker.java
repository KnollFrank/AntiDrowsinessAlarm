package de.antidrowsinessalarm;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import de.antidrowsinessalarm.event.UpdateEvent;
import de.antidrowsinessalarm.eventproducer.DrowsyEventProducer;

public class GraphicFaceTracker extends Tracker<Face> {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private long delta;
    private boolean firstCallToOnUpdate = true;

    public GraphicFaceTracker(final EventBus eventBus, final DrowsyEventProducer drowsyEventProducer) {
        this.eventBus = eventBus;
        this.drowsyEventProducer = drowsyEventProducer;
    }

    // TODO: Umrechnung zwischen detections.getFrameMetadata().getTimestampMillis() und System.currentTimeMillis() in einer Klasse behandeln.
    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if(this.firstCallToOnUpdate) {
            this.delta = detections.getFrameMetadata().getTimestampMillis() - System.currentTimeMillis();
            this.firstCallToOnUpdate = false;
        }
        this.eventBus.post(new UpdateEvent(detections, face));
        this.drowsyEventProducer.maybeProduceDrowsyEvent(System.currentTimeMillis() + this.delta);
    }
}
