package de.antidrowsinessalarm;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import de.antidrowsinessalarm.event.UpdateEvent;
import de.antidrowsinessalarm.eventproducer.DrowsyEventProducer;

public class GraphicFaceTracker extends Tracker<Face> {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final Clock clock;

    private ClockTime2FrameTimeConverter timeConverter;

    public GraphicFaceTracker(final EventBus eventBus, final DrowsyEventProducer drowsyEventProducer, final Clock clock) {
        this.eventBus = eventBus;
        this.drowsyEventProducer = drowsyEventProducer;
        this.clock = clock;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        final Instant clockTime = this.clock.now();
        if(this.timeConverter == null) {
            this.timeConverter =
                    ClockTime2FrameTimeConverter.fromClockTimeAndFrameTime(
                            clockTime,
                            new Instant(detections.getFrameMetadata().getTimestampMillis()));
        }
        this.eventBus.post(new UpdateEvent(detections, face));
        this.drowsyEventProducer.maybeProduceDrowsyEvent(this.timeConverter.convertToFrameTime(clockTime));
    }
}
