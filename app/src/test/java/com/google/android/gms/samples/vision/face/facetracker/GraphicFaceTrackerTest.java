package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.NormalEyeBlinkEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.android.gms.samples.vision.face.facetracker.listener.NormalEyeBlinkEventProducer;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.android.gms.vision.Frame.Metadata;
import com.google.common.eventbus.Subscribe;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

public class GraphicFaceTrackerTest {

    private static class EventListener {

        private Event event;

        @Subscribe
        public void recordEvent(Event event) {
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }
    }

    @Test
    public void shouldCreateEyesClosedEvent() {
        shouldCreateEvent(0.4f, 0.4f, new EyesClosedEvent(100));
    }

    @Test
    public void shouldCreateEyesClosedEvent2() {
        shouldCreateEvent(0.3f, 0.4f, new EyesClosedEvent(101));
    }

    private void shouldCreateEvent(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability, final Event event) {
        // Given
        EventListener listener = new EventListener();
        EventBus eventBus = new EventBus();
        eventBus.register(listener);

        Tracker<Face> tracker = new GraphicFaceTracker(eventBus);

        // When
        tracker.onUpdate(getFaceDetections(event.getTimestampMillis()), createFace(isLeftEyeOpenProbability, isRightEyeOpenProbability));

        // Then
        assertThat(listener.getEvent(), is(event));
    }

    @Test
    public void shouldCreateEyesOpenedClosedEvent() {
        shouldCreateEvent(0.8f, 0.8f, new EyesOpenedEvent(123));
    }

    @Test
    public void shouldCreateEyesOpenedClosedEvent2() {
        shouldCreateEvent(0.8f, 0.9f, new EyesOpenedEvent(1234));
    }

    @Test
    public void shouldCreateNormalEyeBlink() {
        // Given
        EventListener listener = new EventListener();
        EventBus eventBus = new EventBus();
        eventBus.register(listener);
        eventBus.register(new NormalEyeBlinkEventProducer(eventBus));

        Tracker<Face> tracker = new GraphicFaceTracker(eventBus);

        // When
        tracker.onUpdate(getFaceDetections(0), createFaceWithEyesClosed());
        tracker.onUpdate(getFaceDetections(499l), createFaceWithEyesOpened());

        // Then
        assertThat(listener.getEvent(), Matchers.<Event>is(new NormalEyeBlinkEvent(0, 499)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEvent() {
        // Given
        EventListener listener = new EventListener();
        EventBus eventBus = new EventBus();
        eventBus.register(listener);
        eventBus.register(new NormalEyeBlinkEventProducer(eventBus));

        Tracker<Face> tracker = new GraphicFaceTracker(eventBus);

        // When
        tracker.onUpdate(getFaceDetections(0), createFaceWithEyesClosed());
        tracker.onUpdate(getFaceDetections(501l), createFaceWithEyesOpened());

        // Then
        assertThat(listener.getEvent(), Matchers.<Event>is(new SlowEyelidClosureEvent(0, 501)));
    }

    private Detector.Detections<Face> getFaceDetections(long timestampMillis) {
        Metadata metaData = Mockito.mock(Metadata.class);
        doReturn(timestampMillis).when(metaData).getTimestampMillis();

        Detector.Detections<Face> detections = Mockito.mock(Detector.Detections.class);
        doReturn(metaData).when(detections).getFrameMetadata();

        return detections;
    }

    private Face createFaceWithEyesClosed() {
        return createFace(0.4f, 0.4f);
    }

    private Face createFace(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability) {
        Face face = Mockito.mock(Face.class);
        doReturn(isLeftEyeOpenProbability).when(face).getIsLeftEyeOpenProbability();
        doReturn(isRightEyeOpenProbability).when(face).getIsRightEyeOpenProbability();
        return face;
    }

    private Face createFaceWithEyesOpened() {
        return createFace(0.8f, 0.8f);
    }
}