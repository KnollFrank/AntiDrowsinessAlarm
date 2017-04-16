package de.drowsydriveralarm.eventproducer;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import java.util.Arrays;
import java.util.List;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.event.Event;
import de.drowsydriveralarm.event.EventHelper;

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
        this.maybePostAppActiveEvent(this.clock.now());
    }

    @Override
    public void onUpdate(final Detector.Detections<Face> detections, final Face face) {
        if (!this.areBothEyesRecognized(face)) {
            this.eventBus.post(new AppIdleEvent(EventHelper.getInstantOf(detections)));
        } else {
            this.maybePostAppActiveEvent(EventHelper.getInstantOf(detections));
        }
    }

    // TODO: DRY this method an dependent ones in EventProducingGraphicFaceTracker
    private boolean areBothEyesRecognized(final Face face) {
        return this.getLandmarkTypes(face.getLandmarks()).containsAll(this.getBothEyes());
    }

    private ImmutableList<Integer> getLandmarkTypes(final List<Landmark> landmarks) {
        return FluentIterable
                .from(landmarks)
                .transform(
                        new Function<Landmark, Integer>() {

                            @Override
                            public Integer apply(final Landmark landmark) {
                                return landmark.getType();
                            }
                        })
                .toList();
    }

    @NonNull
    private List<Integer> getBothEyes() {
        return Arrays.asList(Landmark.LEFT_EYE, Landmark.RIGHT_EYE);
    }

    @Override
    public void onMissing(final Detector.Detections<Face> detections) {
        this.maybePostAppIdleEvent(EventHelper.getInstantOf(detections));
    }

    @Override
    public void onDone() {
        this.maybePostAppIdleEvent(this.clock.now());
    }

    private void maybePostAppActiveEvent(final Instant instant) {
        this.onTransitionFromUnknownOrIdle2ActivePostEvent(this.createAppActiveEvent(instant));
    }

    private void maybePostAppIdleEvent(final Instant instant) {
        this.onTransitionFromUnknownOrActive2IdlePostEvent(this.createAppIdleEvent(instant));
    }

    private void onTransitionFromUnknownOrIdle2ActivePostEvent(final Supplier<Event> eventSupplier) {
        if (this.activeState.isUnknown() || this.activeState.isIdle()) {
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
}
