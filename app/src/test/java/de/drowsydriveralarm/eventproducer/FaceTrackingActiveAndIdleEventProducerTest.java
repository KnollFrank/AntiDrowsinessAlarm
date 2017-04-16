package de.drowsydriveralarm.eventproducer;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.common.eventbus.EventBus;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.CompositeFaceTracker;
import de.drowsydriveralarm.EventListener;
import de.drowsydriveralarm.MockedClock;
import de.drowsydriveralarm.SystemClock;
import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.event.Event;

import static de.drowsydriveralarm.eventproducer.EventProducingGraphicFaceTrackerTest.createFaceWithEyesClosed;
import static de.drowsydriveralarm.eventproducer.EventProducingGraphicFaceTrackerTest.createFaceWithEyesOpened;
import static de.drowsydriveralarm.eventproducer.EventProducingGraphicFaceTrackerTest.getFaceDetections;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.doReturn;

public class FaceTrackingActiveAndIdleEventProducerTest {

    private EventListener eventListener;
    private Tracker<Face> tracker;

    @Before
    public void setup() {
        this.setup(new SystemClock());
    }

    public void setup(final Clock clock) {
        // Given
        this.eventListener = new EventListener();
        final EventBus eventBus = new EventBus();
        eventBus.register(this.eventListener);
        final IDrowsyEventDetectorConfig config = new TestingDrowsyEventDetectorConfig(SharedPreferencesTestFactory.createSharedPreferences());
        final EventSubscriberProvider eventSubscriberProvider = new EventSubscriberProvider(eventBus, config);
        DrowsyEventDetector.registerEventSubscribersOnEventBus(eventSubscriberProvider.getEventSubscribers(), eventBus);

        this.tracker =
                new CompositeFaceTracker(
                        new FaceTrackingActiveAndIdleEventProducer(eventBus, clock),
                        new EventProducingGraphicFaceTracker(
                                eventBus,
                                new DrowsyEventProducer(
                                        config.getConfig(),
                                        eventBus,
                                        eventSubscriberProvider.getSlowEyelidClosureEventsProvider()),
                                clock));
    }

    @Test
    public void shouldCreateAppActiveEvent_onNewItem() {
        // When
        this.tracker.onNewItem(1, createFaceWithEyesClosed());

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppActiveEvent.class)));
    }

    @Test
    public void shouldCreateAppIdleEvent_onMissing() {
        // When
        this.tracker.onMissing(getFaceDetections(new Instant(501)));

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppIdleEvent.class)));
    }

    @Test
    public void shouldCreateAppIdleEvent_onUpdate_onMissing() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(0)), createFaceWithEyesClosed());
        this.tracker.onMissing(getFaceDetections(new Instant(501)));

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppIdleEvent.class)));
    }

    @Test
    public void shouldCreateAppIdleEvent_onDone() {
        // When
        this.tracker.onDone();

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppIdleEvent.class)));
    }

    @Test
    public void testCameraPartiallyCovered() {
        // When
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        clock.setNow(new Instant(0));
        this.tracker.onNewItem(1, createFaceWithEyesClosed());

        clock.setNow(new Instant(10));
        this.tracker.onUpdate(getFaceDetections(new Instant(10)), createFaceWithEyesClosed());

        clock.setNow(new Instant(20));
        this.tracker.onMissing(getFaceDetections(new Instant(20)));

        clock.setNow(new Instant(30));
        this.tracker.onUpdate(getFaceDetections(new Instant(30)), createFaceWithEyesClosed());

        clock.setNow(new Instant(90));
        this.tracker.onUpdate(getFaceDetections(new Instant(90)), createFaceWithEyesClosed());

        clock.setNow(new Instant(100));
        this.tracker.onMissing(getFaceDetections(new Instant(100)));

        clock.setNow(new Instant(110));
        this.tracker.onUpdate(getFaceDetections(new Instant(110)), createFaceWithEyesClosed());

        clock.setNow(new Instant(120));
        this.tracker.onMissing(getFaceDetections(new Instant(120)));

        clock.setNow(new Instant(130));
        this.tracker.onMissing(getFaceDetections(new Instant(130)));

        clock.setNow(new Instant(140));
        this.tracker.onDone();

        // Then
        assertThat(
                this.eventListener.filterEventsBy(AppActiveEvent.class, AppIdleEvent.class),
                contains(
                        new AppActiveEvent(new Instant(0)),
                        new AppIdleEvent(new Instant(20)),
                        new AppActiveEvent(new Instant(30)),
                        new AppIdleEvent(new Instant(100)),
                        new AppActiveEvent(new Instant(110)),
                        new AppIdleEvent(new Instant(120))));
    }

    @Test
    public void testFaceDisappearsAndReappears() {
        // When
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        clock.setNow(new Instant(0));
        this.tracker.onMissing(getFaceDetections(new Instant(0)));

        clock.setNow(new Instant(10));
        this.tracker.onMissing(getFaceDetections(new Instant(10)));

        clock.setNow(new Instant(20));
        this.tracker.onDone();

        clock.setNow(new Instant(30));
        this.tracker.onNewItem(1, createFaceWithEyesClosed());

        clock.setNow(new Instant(40));
        this.tracker.onUpdate(getFaceDetections(new Instant(40)), createFaceWithEyesClosed());

        clock.setNow(new Instant(50));
        this.tracker.onUpdate(getFaceDetections(new Instant(50)), createFaceWithEyesClosed());

        clock.setNow(new Instant(60));
        this.tracker.onMissing(getFaceDetections(new Instant(60)));

        clock.setNow(new Instant(70));
        this.tracker.onMissing(getFaceDetections(new Instant(70)));

        clock.setNow(new Instant(80));
        this.tracker.onDone();

        // Then
        assertThat(
                this.eventListener.filterEventsBy(AppActiveEvent.class, AppIdleEvent.class),
                contains(
                        new AppIdleEvent(new Instant(0)),
                        new AppActiveEvent(new Instant(30)),
                        new AppIdleEvent(new Instant(60))));
    }

    @Test
    public void testStareAtCameraAndThenGoAway() {
        // When
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        clock.setNow(new Instant(0));
        this.tracker.onNewItem(1, createFaceWithEyesClosed());

        clock.setNow(new Instant(10));
        this.tracker.onUpdate(getFaceDetections(new Instant(10)), createFaceWithEyesOpened());

        clock.setNow(new Instant(20));
        this.tracker.onUpdate(getFaceDetections(new Instant(20)), createFaceWithEyesOpened());

        clock.setNow(new Instant(30));
        this.tracker.onUpdate(getFaceDetections(new Instant(30)), createFaceWithEyesOpened());

        clock.setNow(new Instant(40));
        this.tracker.onUpdate(getFaceDetections(new Instant(40)), createFaceWithEyesOpened());

        clock.setNow(new Instant(50));
        this.tracker.onUpdate(getFaceDetections(new Instant(50)), createFaceWithEyesOpened());

        clock.setNow(new Instant(60));
        this.tracker.onMissing(getFaceDetections(new Instant(60)));

        clock.setNow(new Instant(70));
        this.tracker.onMissing(getFaceDetections(new Instant(70)));

        clock.setNow(new Instant(80));
        this.tracker.onDone();

        // Then
        assertThat(
                this.eventListener.filterEventsBy(AppActiveEvent.class, AppIdleEvent.class),
                contains(
                        new AppActiveEvent(new Instant(0)),
                        new AppIdleEvent(new Instant(60))));
    }

    @Test
    public void shouldCreateAppIdleEventWhenFaceRecognizedButEyesNotRecognized() {
        this.shouldCreateNoEventsForFaceWithLandmarks(Collections.<Landmark> emptyList());
    }

    @Test
    public void shouldCreateAppIdleEventWhenFaceRecognizedButLEFT_EYENotRecognized() {
        this.shouldCreateNoEventsForFaceWithLandmarks(Arrays.asList(createLandmark(Landmark.RIGHT_EYE)));
    }

    private void shouldCreateNoEventsForFaceWithLandmarks(final List<Landmark> landmarks) {
        // When
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        clock.setNow(new Instant(0));
        this.tracker.onUpdate(getFaceDetections(new Instant(0)), this.createFaceWithLandmarks(landmarks));

        // Then
        assertThat(
                this.eventListener.filterEventsBy(AppActiveEvent.class, AppIdleEvent.class),
                IsIterableContainingInOrder.<Event> contains(
                        new AppIdleEvent(new Instant(0))));
    }

    // TODO: DRY mit EventProducingGraphicFaceTrackerTest
    @NonNull
    private static Landmark createLandmark(final int type) {
        return new Landmark(new PointF(1, 1), type);
    }

    private Face createFaceWithLandmarks(final List<Landmark> landmarks) {
        final Face face = Mockito.mock(Face.class);
        doReturn(landmarks).when(face).getLandmarks();
        return face;
    }
}
