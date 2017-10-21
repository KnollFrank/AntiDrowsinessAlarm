package de.drowsydriveralarm;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import java.util.Arrays;

import de.drowsydriveralarm.event.SlowEyelidClosureEvent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PERCLOSCalculatorTest {

    @Test
    public void shouldCalculatePERCLOS_allEventsCompletelyWithinTimewindow() {
        // Given
        final PERCLOSCalculator perclosCalculator = new PERCLOSCalculator(new Duration(2000));

        // When
        final double perclos =
                perclosCalculator.calculatePERCLOS(
                        Arrays.asList(
                                new SlowEyelidClosureEvent(new Instant(100), new Duration(600)),
                                new SlowEyelidClosureEvent(new Instant(1000), new Duration(550))),
                        new Instant(2000));

        // Then
        assertThat(perclos, is((600.0 + 550.0) / 2000.0));
    }

    @Test
    public void shouldCalculatePERCLOS_firstEventPartlyWithinTimewindow() {
        // Given
        final PERCLOSCalculator perclosCalculator = new PERCLOSCalculator(new Duration(10));

        // When
        final double perclos =
                perclosCalculator.calculatePERCLOS(
                        Arrays.asList(
                                new SlowEyelidClosureEvent(new Instant(0), new Duration(5)),
                                new SlowEyelidClosureEvent(new Instant(7), new Duration(5))),
                        new Instant(12));

        // Then
        // intersection of Event [0, 5] with timeWindow [2, 12] = [2, 5] which has length 3.0
        assertThat(perclos, is((3.0 + 5.0) / 10.0));
    }
}
