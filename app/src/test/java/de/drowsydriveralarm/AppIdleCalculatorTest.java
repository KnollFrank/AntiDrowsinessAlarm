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
    public void shouldGetAppIdleDuration1() {
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

    @Test
    public void shouldGetAppIdleDuration3() {
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(0)),
                        new AppActiveEvent(new Instant(10)),
                        new AppIdleEvent(new Instant(50)),
                        new AppActiveEvent(new Instant(70))),
                new Instant(80),
                new Duration((10 - 0) + (70 - 50)));
    }

    @Test
    public void shouldGetAppIdleDuration4() {
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(10))),
                new Instant(15),
                new Duration(5));
    }

    private void shouldGetAppIdleDuration(final List<? extends Event> events, final Instant now, final Duration appIdleDurationExpected) {
        // Given
        final EventBus eventBus = new EventBus();
        final AppIdleCalculator appIdleCalculator = new AppIdleCalculator();
        eventBus.register(appIdleCalculator);

        // When
        for (final Event event : events) {
            eventBus.post(event);
        }

        // Then
        assertThat(appIdleCalculator.getAppIdleDuration(now), is(appIdleDurationExpected));
    }
}
