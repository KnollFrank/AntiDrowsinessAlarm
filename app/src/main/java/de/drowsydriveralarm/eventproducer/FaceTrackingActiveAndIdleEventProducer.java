package de.drowsydriveralarm.eventproducer;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;

public class FaceTrackingActiveAndIdleEventProducer extends Tracker<Face> {

    private final EventBus eventBus;
    private final Clock clock;
    private final ActiveState activeState = new ActiveState();

    public FaceTrackingActiveAndIdleEventProducer(final EventBus eventBus, final Clock clock) {
        this.eventBus = eventBus;
        this.clock = clock;
    }

    @Override
    public void onNewItem(final int i, final Face face) {
        this.onTransitionFromUnknownOrIdle2Active(this.createAppActiveEvent(this.clock.now()));
    }

    @Override
    public void onUpdate(final Detector.Detections<Face> detections, final Face face) {
        this.onTransitionFromUnknownOrIdle2Active(this.createAppActiveEvent(this.getInstant(detections)));
    }

    @Override
    public void onMissing(final Detector.Detections<Face> detections) {
        this.onTransitionFromUnknownOrActive2Idle(this.createAppIdleEvent(this.getInstant(detections)));
    }

    @Override
    public void onDone() {
        this.onTransitionFromUnknownOrActive2Idle(this.createAppIdleEvent(this.clock.now()));
    }

    private void onTransitionFromUnknownOrIdle2Active(final Runnable runnable) {
        if(this.activeState.isUnknown() || this.activeState.isIdle()) {
            this.activeState.setActive();
            runnable.run();
        }
    }

    private void onTransitionFromUnknownOrActive2Idle(final Runnable runnable) {
        if (this.activeState.isUnknown() || this.activeState.isActive()) {
            this.activeState.setIdle();
            runnable.run();
        }
    }

    @NonNull
    private Runnable createAppActiveEvent(final Instant instant) {
        return new Runnable() {
            @Override
            public void run() {
                FaceTrackingActiveAndIdleEventProducer.this.eventBus.post(
                        new AppActiveEvent(instant));
            }
        };
    }

    @NonNull
    private Runnable createAppIdleEvent(final Instant instant) {
        return new Runnable() {
            @Override
            public void run() {
                FaceTrackingActiveAndIdleEventProducer.this.eventBus.post(
                        new AppIdleEvent(instant));
            }
        };
    }

    @NonNull
    private Instant getInstant(Detector.Detections<Face> detections) {
        return new Instant(detections.getFrameMetadata().getTimestampMillis());
    }
}
