package de.drowsydriveralarm;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.eventproducer.ActiveAndIdleEventProducer;
import de.drowsydriveralarm.eventproducer.DefaultConfigFactory;
import de.drowsydriveralarm.eventproducer.DrowsyEventProducer;
import de.drowsydriveralarm.eventproducer.EyesClosedEventProducer;
import de.drowsydriveralarm.eventproducer.EyesOpenedEventProducer;
import de.drowsydriveralarm.eventproducer.NormalEyeBlinkEventProducer;
import de.drowsydriveralarm.eventproducer.SlowEyelidClosureEventProducer;
import de.drowsydriveralarm.eventproducer.SlowEyelidClosureEventsProvider;

import static de.drowsydriveralarm.EventProducingGraphicFaceTrackerTest.createFaceWithEyesClosed;
import static de.drowsydriveralarm.EventProducingGraphicFaceTrackerTest.getFaceDetections;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.isA;

public class ActiveAndIdleEventProducerTest {

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
        final DefaultConfigFactory configFactory = new DefaultConfigFactory(SharedPreferencesTestFactory.createSharedPreferences());
        // TODO: DRY with DrowsyEventDetector
        eventBus.register(new NormalEyeBlinkEventProducer(configFactory.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new SlowEyelidClosureEventProducer(configFactory.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new EyesOpenedEventProducer(configFactory.getEyeOpenProbabilityThreshold(), eventBus));
        eventBus.register(new EyesClosedEventProducer(configFactory.getEyeOpenProbabilityThreshold(), eventBus));

        this.tracker =
                new CompositeFaceTracker(
                        new ActiveAndIdleEventProducer(eventBus, clock),
                        new EventProducingGraphicFaceTracker(
                                eventBus,
                                new DrowsyEventProducer(
                                        configFactory.getConfig(),
                                        eventBus,
                                        new SlowEyelidClosureEventsProvider(configFactory.getTimeWindow())),
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

    // folgende dazwischen befindliche onMissing Ereignisse kommen vor, falls man einen Teil der Kamera zudeckt, so dass das ganze Gesicht
    // momentan nicht erkannt werden kann, aber nach dem Wiederaufdecken der Kamera das Gesicht mit onUpdate wieder erkannt wird:
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

    // altes Gesicht verschwindet (onMissing, onDone 22:15:07), neues Gesicht wird erkannt (22:15:19 onNewItem):
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

// Gesicht der Kamera zeigen und wieder aus dem Kamerasichtfeld entfernen:
    @Test
    public void testStarAtCameraAndThenGoAway() {
        // When
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        clock.setNow(new Instant(0));
        this.tracker.onNewItem(1, createFaceWithEyesClosed());

        clock.setNow(new Instant(10));
        this.tracker.onUpdate(getFaceDetections(new Instant(10)), createFaceWithEyesClosed());

        clock.setNow(new Instant(20));
        this.tracker.onUpdate(getFaceDetections(new Instant(20)), createFaceWithEyesClosed());

        clock.setNow(new Instant(30));
        this.tracker.onUpdate(getFaceDetections(new Instant(30)), createFaceWithEyesClosed());

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
                        new AppActiveEvent(new Instant(0)),
                        new AppIdleEvent(new Instant(60))));
    }
}
