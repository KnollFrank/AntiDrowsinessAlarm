package de.drowsydriveralarm.eventproducer;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.event.EventHelper;
import de.drowsydriveralarm.event.UpdateEvent;

public class EventProducingGraphicFaceTracker extends Tracker<Face> {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final Clock clock;

    private ClockTime2FrameTimeConverter timeConverter;

    public EventProducingGraphicFaceTracker(final EventBus eventBus, final DrowsyEventProducer drowsyEventProducer, final Clock clock) {
        this.eventBus = eventBus;
        this.drowsyEventProducer = drowsyEventProducer;
        this.clock = clock;
    }

    @Override
    public void onUpdate(final Detector.Detections<Face> detections, final Face face) {
        // TODO: use RetroLambda (https://github.com/orfjackal/retrolambda)
        final Instant clockTime = this.clock.now();
        if (this.timeConverter == null) {
            this.timeConverter = ClockTime2FrameTimeConverter.fromClockTimeAndFrameTime(clockTime, EventHelper.getInstantOf(detections));
        }

        if (!BothEyesRecognizedPredicate.areBothEyesRecognized(face)) {
            return;
        }

        this.eventBus.post(new UpdateEvent(detections, face));
        this.drowsyEventProducer.maybeProduceDrowsyEvent(this.timeConverter.convertToFrameTime(clockTime));
    }
}
