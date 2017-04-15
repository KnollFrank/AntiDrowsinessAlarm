package de.drowsydriveralarm;

import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.event.Event;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppIdleCalculatorTest {

    private EventBus eventBus;
    private AppIdleCalculator appIdleCalculator;

    @Before
    public void seetup() {
        this.eventBus = new EventBus();
        this.appIdleCalculator = new AppIdleCalculator();
        this.eventBus.register(this.appIdleCalculator);
    }

    @Test
    public void shouldGetAppIdleDuration_oneAppIdleInterval_nowIsPastIntervalEnd() {
        final int intervalEnd = 10;
        final int now = intervalEnd + 5;
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(0)),
                        new AppActiveEvent(new Instant(intervalEnd))),
                new Instant(now),
                new Duration(10));
    }

    @Test
    public void shouldGetAppIdleDuration_oneAppIdleInterval_nowIsAtIntervalEnd() {
        final int intervalEnd = 10;
        final int now = intervalEnd;
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(0)),
                        new AppActiveEvent(new Instant(intervalEnd))),
                new Instant(now),
                new Duration(10));
    }

    @Test
    public void shouldGetAppIdleDuration_appIdleDurationIsZero() {
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppActiveEvent(new Instant(10))),
                new Instant(15),
                new Duration(0));
    }

    @Test
    public void shouldGetAppIdleDuration_oneAppIdleInterval_nowIsPastIntervalEnd2() {
        final int intervalEnd = 25;
        final int now = intervalEnd + 2;
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(20)),
                        new AppActiveEvent(new Instant(intervalEnd))),
                new Instant(now),
                new Duration(5));
    }

    @Test
    public void shouldGetAppIdleDuration_twoAppIdleIntervals() {
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
    public void shouldGetAppIdleDuration_onePendingAppIdleInterval() {
        this.shouldGetAppIdleDuration(
                Arrays.asList(
                        new AppIdleEvent(new Instant(10))),
                new Instant(15),
                new Duration(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetAppIdleDurationForPast() {
        this.eventBus.post(new AppIdleEvent(new Instant(50)));
        this.appIdleCalculator.getAppIdleDuration(new Instant(40));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetAppIdleDurationForPast2() {
        this.eventBus.post(new AppActiveEvent(new Instant(50)));
        this.appIdleCalculator.getAppIdleDuration(new Instant(40));
    }

    private void shouldGetAppIdleDuration(final List<? extends Event> events, final Instant now, final Duration appIdleDurationExpected) {
        // When
        for (final Event event : events) {
            this.eventBus.post(event);
        }

        final Duration appIdleDurationActual = this.appIdleCalculator.getAppIdleDuration(now);

        // Then
        assertThat(appIdleDurationActual, is(appIdleDurationExpected));
    }
}
