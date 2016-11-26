package de.antidrowsinessalarm.eventproducer;

import org.junit.Test;

import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class SlowEyelidClosureEventsProviderTest {

    @Test
    public void testEventCompletelyWithinTimewindow() {
        // Given
        SlowEyelidClosureEventsProvider eventsProvider = new SlowEyelidClosureEventsProvider(10);

        // When
        eventsProvider.recordSlowEyelidClosureEvent(new SlowEyelidClosureEvent(0, 5));
        List<SlowEyelidClosureEvent> recordedEventsPartlyWithinTimeWindow = eventsProvider.getRecordedEventsPartlyWithinTimeWindow(5);

        // Then
        assertThat(recordedEventsPartlyWithinTimeWindow, contains(new SlowEyelidClosureEvent(0, 5)));
        assertThat(eventsProvider.getEvents(), contains(new SlowEyelidClosureEvent(0, 5)));
    }

    @Test
    public void testEventPartlyWithinTimewindow() {
        // Given
        SlowEyelidClosureEventsProvider eventsProvider = new SlowEyelidClosureEventsProvider(10);

        // When
        eventsProvider.recordSlowEyelidClosureEvent(new SlowEyelidClosureEvent(0, 5));
        List<SlowEyelidClosureEvent> recordedEventsPartlyWithinTimeWindow = eventsProvider.getRecordedEventsPartlyWithinTimeWindow(12);

        // Then
        assertThat(recordedEventsPartlyWithinTimeWindow, contains(new SlowEyelidClosureEvent(0, 5)));
        assertThat(eventsProvider.getEvents(), contains(new SlowEyelidClosureEvent(0, 5)));
    }
}
