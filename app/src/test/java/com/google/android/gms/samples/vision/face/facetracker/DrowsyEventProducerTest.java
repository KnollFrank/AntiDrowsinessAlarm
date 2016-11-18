package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.DrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.android.gms.samples.vision.face.facetracker.listener.DrowsyEventProducer;
import com.google.common.eventbus.EventBus;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DrowsyEventProducerTest {

    private GraphicFaceTrackerTest.EventListener listener;

    @Test
    public void shouldCreateDrowsyEvent() {
        // Given
        this.listener = new GraphicFaceTrackerTest.EventListener();
        final EventBus eventBus = new EventBus();
        DrowsyEventProducer drowsyEventProducer = new DrowsyEventProducer(eventBus, 2000);
        eventBus.register(drowsyEventProducer);
        eventBus.register(this.listener);

        eventBus.post(new SlowEyelidClosureEvent(100, 600));
        eventBus.post(new SlowEyelidClosureEvent(1000, 550));

        // When
        drowsyEventProducer.evaluate(2000);

        // Then
        assertThat(this.listener.getEvent(), is((Event) new DrowsyEvent(2000, (600.0 + 550.0) / 2000.0)));
    }

    @Test
    public void shouldCreateLikelyDrowsyEvent() {

    }
}
