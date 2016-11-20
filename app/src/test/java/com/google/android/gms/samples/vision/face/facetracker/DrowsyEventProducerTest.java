package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.DrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.Event;
import com.google.android.gms.samples.vision.face.facetracker.event.LikelyDrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.android.gms.samples.vision.face.facetracker.listener.DrowsyEventProducer;
import com.google.android.gms.samples.vision.face.facetracker.listener.SlowEyelidClosureEventsProvider;
import com.google.common.eventbus.EventBus;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNot.not;

public class DrowsyEventProducerTest {

    private GraphicFaceTrackerTest.EventListener listener;
    private EventBus eventBus;
    private DrowsyEventProducer drowsyEventProducer;

    @Before
    public void setup() {
        this.listener = new GraphicFaceTrackerTest.EventListener();
        this.eventBus = new EventBus();
        final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider();
        this.drowsyEventProducer = new DrowsyEventProducer(this.eventBus, 2000, slowEyelidClosureEventsProvider);
        this.eventBus.register(slowEyelidClosureEventsProvider);
        this.eventBus.register(this.listener);
    }

    @Test
    public void shouldCreateDrowsyEvent() {
        // Given
        this.eventBus.post(new SlowEyelidClosureEvent(100, 600));
        this.eventBus.post(new SlowEyelidClosureEvent(1000, 550));
        double perclos = (600.0 + 550.0) / 2000.0; // = 0.575 > 0.15

        // When
        this.drowsyEventProducer.maybeProduceDrowsyEvent(2000);

        // Then
        assertThat(this.listener.getEvent(), is((Event) new DrowsyEvent(2000, perclos)));
    }

    @Test
    public void shouldCreateLikelyDrowsyEvent() {
        // Given
        this.eventBus.post(new SlowEyelidClosureEvent(100, 150));
        this.eventBus.post(new SlowEyelidClosureEvent(1000, 55));
        double perclos = (150 + 55.0) / 2000.0; // = 0.1025 wich is between 0.08 and 0.15

        // When
        this.drowsyEventProducer.maybeProduceDrowsyEvent(2000);

        // Then
        assertThat(this.listener.getEvent(), is((Event) new LikelyDrowsyEvent(2000, perclos)));
    }

    @Test
    public void shouldCreateNoDrowsyEvents() {
        // Given
        this.eventBus.post(new SlowEyelidClosureEvent(100, 150));
        this.eventBus.post(new SlowEyelidClosureEvent(1000, 55));

        // When
        // given SlowEyelidClosureEvents are not within time window
        this.drowsyEventProducer.maybeProduceDrowsyEvent(5000);

        // Then
        assertThat(this.listener.getEvents(), not(hasItem(isA(LikelyDrowsyEvent.class))));
        assertThat(this.listener.getEvents(), not(hasItem(isA(DrowsyEvent.class))));
    }
}
