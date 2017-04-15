package de.drowsydriveralarm;

import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.event.Event;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppIdleCalculatorTest {

    @Test
    public void shouldGetAppIdleDuration() {
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(0)),
                        new AppActiveEvent(new Instant(10))),
                new Instant(15),
                new Duration(10));
    }

    @Test
    public void shouldGetAppIdleDuration2() {
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(20)),
                        new AppActiveEvent(new Instant(25))),
                new Instant(27),
                new Duration(5));
    }

    private void shouldGetAppIdleDuration(final List<Event> events, final Instant now, final Duration appIdleDurationExpected) {
        // Given
        final EventBus eventBus = new EventBus();
        final AppIdleCalculator appIdleCalculator = new AppIdleCalculator();
        eventBus.register(appIdleCalculator);

        // When
        for(final Event event : events) {
            eventBus.post(event);
        }

        // Then
        assertThat(appIdleCalculator.getAppIdleDuration(now), is(appIdleDurationExpected));
    }
}
