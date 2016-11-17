package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

public class GraphicFaceTrackerTest {

    private static class EventListener {

        private Event event;

        @Subscribe
        public void recordCustomerChange(Event event) {
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }
    }

    @Test
    public void shouldCreateEyesClosedEvent() {
        // Given
        EventListener listener = new EventListener();
        EventBus eventBus = new EventBus();
        eventBus.register(listener);

        Tracker<Face> tracker = new GraphicFaceTracker(eventBus);
        Face face = Mockito.mock(Face.class);
        doReturn(0.4f).when(face).getIsRightEyeOpenProbability();
        doReturn(0.4f).when(face).getIsLeftEyeOpenProbability();

        // When
       tracker.onUpdate(null, face);

        // Then
        assertThat(listener.getEvent(), is(Event.EyesClosedEvent));
    }

    @Test
    public void shouldCreateEyesOpenedClosedEvent() {
        // Given
        EventListener listener = new EventListener();
        EventBus eventBus = new EventBus();
        eventBus.register(listener);

        Tracker<Face> tracker = new GraphicFaceTracker(eventBus);
        Face face = Mockito.mock(Face.class);
        doReturn(0.8f).when(face).getIsRightEyeOpenProbability();
        doReturn(0.8f).when(face).getIsLeftEyeOpenProbability();

        // When
        tracker.onUpdate(null, face);

        // Then
        assertThat(listener.getEvent(), is(Event.EyesOpenedEvent));
    }
}