package de.drowsydriveralarm.eventproducer;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.Supplier;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.event.Event;

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
        this.onTransitionFromUnknownOrIdle2ActivePostEvent(this.createAppActiveEvent(this.clock.now()));
    }

    @Override
    public void onUpdate(final Detector.Detections<Face> detections, final Face face) {
        this.onTransitionFromUnknownOrIdle2ActivePostEvent(this.createAppActiveEvent(this.getInstant(detections)));
    }

    @Override
    public void onMissing(final Detector.Detections<Face> detections) {
        this.onTransitionFromUnknownOrActive2IdlePostEvent(this.createAppIdleEvent(this.getInstant(detections)));
    }

    @Override
    public void onDone() {
        this.onTransitionFromUnknownOrActive2IdlePostEvent(this.createAppIdleEvent(this.clock.now()));
    }

    private void onTransitionFromUnknownOrIdle2ActivePostEvent(final Supplier<Event> eventSupplier) {
        if(this.activeState.isUnknown() || this.activeState.isIdle()) {
            this.activeState.setActive();
            this.eventBus.post(eventSupplier.get());
        }
    }

    private void onTransitionFromUnknownOrActive2IdlePostEvent(final Supplier<Event> eventSupplier) {
        if (this.activeState.isUnknown() || this.activeState.isActive()) {
            this.activeState.setIdle();
            this.eventBus.post(eventSupplier.get());
        }
    }

    @NonNull
    private Supplier<Event> createAppActiveEvent(final Instant instant) {
        return new Supplier<Event>() {

            @Override
            public Event get() {
                return new AppActiveEvent(instant);
            }
        };
    }

    @NonNull
    private Supplier<Event> createAppIdleEvent(final Instant instant) {
        return new Supplier<Event>() {

            @Override
            public Event get() {
                return new AppIdleEvent(instant);
            }
        };
    }

    @NonNull
    private Instant getInstant(Detector.Detections<Face> detections) {
        return new Instant(detections.getFrameMetadata().getTimestampMillis());
    }
}
