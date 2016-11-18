package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.NormalEyeBlinkEvent;
import com.google.android.gms.samples.vision.face.facetracker.listener.NormalEyeBlinkEventProducer;
import com.google.android.gms.vision.Detector;
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

    private void shouldCreateEvent(float isLeftEyeOpenProbability, float isRightEyeOpenProbability, Event event) {
        // Given
        EventListener listener = new EventListener();
        EventBus eventBus = new EventBus();
        eventBus.register(listener);

        Tracker<Face> tracker = new GraphicFaceTracker(eventBus);

        Face face = Mockito.mock(Face.class);
        doReturn(isLeftEyeOpenProbability).when(face).getIsLeftEyeOpenProbability();
        doReturn(isRightEyeOpenProbability).when(face).getIsRightEyeOpenProbability();

        Metadata metaData = Mockito.mock(Metadata.class);
        doReturn(event.getTimestampMillis()).when(metaData).getTimestampMillis();
        Detector.Detections<Face> detections = Mockito.mock(Detector.Detections.class);
        doReturn(metaData).when(detections).getFrameMetadata();

        // When
        tracker.onUpdate(detections, face);

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

        Face face = Mockito.mock(Face.class);
        doReturn(0.4f).when(face).getIsLeftEyeOpenProbability();
        doReturn(0.4f).when(face).getIsRightEyeOpenProbability();

        Metadata metaData = Mockito.mock(Metadata.class);
        doReturn(0l).when(metaData).getTimestampMillis();
        Detector.Detections<Face> detections = Mockito.mock(Detector.Detections.class);
        doReturn(metaData).when(detections).getFrameMetadata();

        Face face2 = Mockito.mock(Face.class);
        doReturn(0.8f).when(face2).getIsLeftEyeOpenProbability();
        doReturn(0.8f).when(face2).getIsRightEyeOpenProbability();

        Metadata metaData2 = Mockito.mock(Metadata.class);
        doReturn(499l).when(metaData2).getTimestampMillis();
        Detector.Detections<Face> detections2 = Mockito.mock(Detector.Detections.class);
        doReturn(metaData2).when(detections2).getFrameMetadata();

        // When
        tracker.onUpdate(detections, face);
        tracker.onUpdate(detections2, face2);

        // Then
        assertThat(listener.getEvent(), Matchers.<Event>is(new NormalEyeBlinkEvent(0, 499)));
    }
}