package de.antidrowsinessalarm.eventproducer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class SlowEyelidClosureEventsProviderTest {

    private SlowEyelidClosureEventsProvider eventsProvider;

    @Before
    public void setup() {
        this.eventsProvider = new SlowEyelidClosureEventsProvider(10);
    }

    @Test
    public void testEventCompletelyWithinTimewindow() {
        // Given
        final SlowEyelidClosureEvent event = new SlowEyelidClosureEvent(0, 5);

        // When
        this.eventsProvider.recordSlowEyelidClosureEvent(event);
        List<SlowEyelidClosureEvent> recordedEventsPartlyWithinTimeWindow = this.eventsProvider.getRecordedEventsPartlyWithinTimeWindow(5);

        // Then
        assertThat(recordedEventsPartlyWithinTimeWindow, contains(event));
        assertThat(this.eventsProvider.getEvents(), contains(event));
    }

    @Test
    public void testEventPartlyWithinTimewindow() {
        // Given
        final SlowEyelidClosureEvent event = new SlowEyelidClosureEvent(0, 5);

        // When
        this.eventsProvider.recordSlowEyelidClosureEvent(event);
        List<SlowEyelidClosureEvent> recordedEventsPartlyWithinTimeWindow = this.eventsProvider.getRecordedEventsPartlyWithinTimeWindow(12);

        // Then
        assertThat(recordedEventsPartlyWithinTimeWindow, contains(event));
        assertThat(this.eventsProvider.getEvents(), contains(event));
    }
}
