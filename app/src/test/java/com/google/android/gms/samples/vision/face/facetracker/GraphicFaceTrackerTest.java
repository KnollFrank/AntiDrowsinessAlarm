package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.NormalEyeBlinkEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.android.gms.samples.vision.face.facetracker.listener.DrowsyEventProducer;
import com.google.android.gms.samples.vision.face.facetracker.listener.EyesClosedEventProducer;
import com.google.android.gms.samples.vision.face.facetracker.listener.EyesOpenedEventProducer;
import com.google.android.gms.samples.vision.face.facetracker.listener.NormalEyeBlinkEventProducer;
import com.google.android.gms.samples.vision.face.facetracker.listener.SlowEyelidClosureEventProducer;
import com.google.android.gms.samples.vision.face.facetracker.listener.SlowEyelidClosureEventsProvider;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame.Metadata;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.doReturn;

public class GraphicFaceTrackerTest {

    private EventListener listener;
    private Tracker<Face> tracker;

    @Before
    public void setup() {
        // Given
        this.listener = new EventListener();
        final EventBus eventBus = new EventBus();
        eventBus.register(this.listener);
        eventBus.register(new NormalEyeBlinkEventProducer(eventBus));
        eventBus.register(new SlowEyelidClosureEventProducer(eventBus));
        eventBus.register(new EyesOpenedEventProducer(eventBus));
        eventBus.register(new EyesClosedEventProducer(eventBus));

        this.tracker = new GraphicFaceTracker(eventBus, new DrowsyEventProducer(eventBus, 15000, new SlowEyelidClosureEventsProvider()));
    }

    @Test
    public void shouldCreateEyesClosedEvent() {
        this.shouldCreateEvent(0.4f, 0.4f, new EyesClosedEvent(100));
    }

    @Test
    public void shouldCreateEyesClosedEvent2() {
        this.shouldCreateEvent(0.3f, 0.4f, new EyesClosedEvent(101));
    }

    private void shouldCreateEvent(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability, final Event event) {
        // When
        this.tracker.onUpdate(this.getFaceDetections(event.getTimestampMillis()), this.createFace(isLeftEyeOpenProbability, isRightEyeOpenProbability));

        // Then
        assertThat(this.listener.getEvent(), is(event));
    }

    @Test
    public void shouldNotCreateEyesClosedEventOnUNCOMPUTED_PROBABILITIES() {
        // When
        this.tracker.onUpdate(this.getFaceDetections(100), this.createFace(Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY));

        // Then
        assertThat(this.listener.getEvents(), not(hasItem(Matchers.<Event>instanceOf(EyesClosedEvent.class))));
    }

    @Test
    public void shouldNotCreateEyesOpenedEventOnUNCOMPUTED_PROBABILITIES() {
        // When
        this.tracker.onUpdate(this.getFaceDetections(100), this.createFace(Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY));

        // Then
        assertThat(this.listener.getEvents(), not(hasItem(Matchers.<Event>instanceOf(EyesOpenedEvent.class))));
    }

    @Test
    public void shouldCreateEyesOpenedEvent() {
        this.shouldCreateEvent(0.8f, 0.8f, new EyesOpenedEvent(123));
    }

    @Test
    public void shouldCreateEyesOpenedEvent2() {
        this.shouldCreateEvent(0.8f, 0.9f, new EyesOpenedEvent(1234));
    }

    @Test
    public void shouldCreateNormalEyeBlink() {
        // When
        this.tracker.onUpdate(this.getFaceDetections(0), this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(499), this.createFaceWithEyesOpened());

        // Then
        assertThat(this.listener.getEvent(), Matchers.<Event>is(new NormalEyeBlinkEvent(0, 499)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEvent() {
        // When
        this.tracker.onUpdate(this.getFaceDetections(0), this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(501), this.createFaceWithEyesOpened());

        // Then
        assertThat(this.listener.getEvent(), Matchers.<Event>is(new SlowEyelidClosureEvent(0, 501)));
    }

    @Test
    public void shouldCreateASingleEyesClosedEvent() {
        // When
        this.tracker.onUpdate(this.getFaceDetections(100), this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(101), this.createFaceWithEyesClosed());

        // Then
        assertThat(this.listener.getEvents(), contains((Event) (new EyesClosedEvent(100))));
    }

    @Test
    public void shouldCreateASingleEyesOpenedEvent() {
        // When
        this.tracker.onUpdate(this.getFaceDetections(100), this.createFaceWithEyesOpened());
        this.tracker.onUpdate(this.getFaceDetections(101), this.createFaceWithEyesOpened());

        // Then
        assertThat(this.listener.getEvents(), contains((Event) (new EyesOpenedEvent(100))));
    }

    @Test
    public void shouldCreateEvents() {
        // When
        this.tracker.onNewItem(1, this.createFaceWithEyesOpened());
        this.tracker.onUpdate(this.getFaceDetections(100), this.createFaceWithEyesOpened());
        this.tracker.onUpdate(this.getFaceDetections(101), this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(102), this.createFaceWithEyesOpened());
        this.tracker.onUpdate(this.getFaceDetections(103), this.createFaceWithEyesClosed());

        // Then
        assertThat(this.listener.getEvents(), hasItems(
                new EyesOpenedEvent(100),
                new EyesClosedEvent(101),
                new EyesOpenedEvent(102),
                new EyesClosedEvent(103)));
    }

    @Test
    public void shouldCreateEvents2() {
        // When
        this.tracker.onNewItem(1, this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(100), this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(101), this.createFaceWithEyesOpened());
        this.tracker.onUpdate(this.getFaceDetections(102), this.createFaceWithEyesClosed());
        this.tracker.onUpdate(this.getFaceDetections(103), this.createFaceWithEyesOpened());

        // Then
        assertThat(this.listener.getEvents(), hasItems(
                new EyesClosedEvent(100),
                new EyesOpenedEvent(101),
                new EyesClosedEvent(102),
                new EyesOpenedEvent(103)));
    }

    private Detector.Detections<Face> getFaceDetections(final long timestampMillis) {
        final Metadata metaData = Mockito.mock(Metadata.class);
        doReturn(timestampMillis).when(metaData).getTimestampMillis();

        final Detector.Detections<Face> detections = Mockito.mock(Detector.Detections.class);
        doReturn(metaData).when(detections).getFrameMetadata();

        return detections;
    }

    private Face createFaceWithEyesClosed() {
        return this.createFace(0.4f, 0.4f);
    }

    private Face createFace(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability) {
        final Face face = Mockito.mock(Face.class);
        doReturn(isLeftEyeOpenProbability).when(face).getIsLeftEyeOpenProbability();
        doReturn(isRightEyeOpenProbability).when(face).getIsRightEyeOpenProbability();
        return face;
    }

    private Face createFaceWithEyesOpened() {
        return this.createFace(0.8f, 0.8f);
    }

    static class EventListener {

        private final List<Event> events = new ArrayList<Event>();

        @Subscribe
        public void recordEvent(final Event event) {
            this.events.add(event);
        }

        Event getEvent() {
            return !this.events.isEmpty() ? this.events.get(this.events.size() - 1) : null;
        }

        List<Event> getEvents() {
            return this.events;
        }
    }
}