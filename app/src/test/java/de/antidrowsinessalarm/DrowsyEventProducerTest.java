package de.antidrowsinessalarm;

import com.google.common.eventbus.EventBus;

import org.junit.Before;
import org.junit.Test;

import de.antidrowsinessalarm.event.AwakeEvent;
import de.antidrowsinessalarm.event.DrowsyEvent;
import de.antidrowsinessalarm.event.Event;
import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.LikelyDrowsyEvent;
import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;
import de.antidrowsinessalarm.event.UpdateEvent;
import de.antidrowsinessalarm.eventproducer.DrowsyEventDetector;
import de.antidrowsinessalarm.eventproducer.DrowsyEventProducer;

import static junit.framework.Assert.fail;
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
        DrowsyEventDetector drowsyEventDetector = new DrowsyEventDetector(new SystemClock(), 2000, false);
        this.listener = new GraphicFaceTrackerTest.EventListener();
        this.eventBus = drowsyEventDetector.getEventBus();
        this.drowsyEventProducer = drowsyEventDetector.getDrowsyEventProducer();
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
    public void shouldCreateDrowsyEventForEyesClosedTheWholeTime() {
        // Given
        this.eventBus.post(new EyesClosedEvent(0));
        this.eventBus.post(new UpdateEvent(GraphicFaceTrackerTest.getFaceDetections(2000), GraphicFaceTrackerTest.createFaceWithEyesClosed()));
        double perclos = 1.0; // > 0.15

        // When
        this.drowsyEventProducer.maybeProduceDrowsyEvent(2000);

        // Then
        assertThat(this.listener.getEvent(), is((Event) new DrowsyEvent(2000, perclos)));
    }

    // TODO: we need more tests like shouldCreateDrowsyEventForEyesClosedTheWholeTime()

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
    public void shouldCreateAwakeEvent() {
        // Given

        // When
        this.drowsyEventProducer.maybeProduceDrowsyEvent(2000);

        // Then
        assertThat(this.listener.getEvent(), is((Event) new AwakeEvent(2000, 0)));
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

    @Test
    public void shouldCreateASingleDrowsyEvent() {
        fail("not yet implemented");
    }

    @Test
    public void shouldCreateASingleLikelyDrowsyEvent() {
        fail("not yet implemented");
    }

    @Test
    public void shouldCreateASingleAwakeEvent() {
        fail("not yet implemented");
    }
}
