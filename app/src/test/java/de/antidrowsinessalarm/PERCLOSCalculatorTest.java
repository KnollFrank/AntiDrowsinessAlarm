package de.antidrowsinessalarm;

import org.junit.Test;

import java.util.Arrays;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PERCLOSCalculatorTest {

    @Test
    public void shouldCalculatePERCLOS_allEventsCompletelyWithinTimewindow() {
        // Given
        PERCLOSCalculator perclosCalculator = new PERCLOSCalculator(2000);

        // When
        double perclos = perclosCalculator.calculatePERCLOS(Arrays.asList(new SlowEyelidClosureEvent(100, 600), new SlowEyelidClosureEvent(1000, 550)), 2000);

        // Then
        assertThat(perclos, is((600.0 + 550.0) / 2000.0));
    }

    @Test
    public void shouldCalculatePERCLOS_firstEventPartlyWithinTimewindow() {
        // Given
        PERCLOSCalculator perclosCalculator = new PERCLOSCalculator(10);

        // When
        double perclos = perclosCalculator.calculatePERCLOS(Arrays.asList(new SlowEyelidClosureEvent(0, 5), new SlowEyelidClosureEvent(7, 5)), 12);

        // Then
        // intersection of Event [0, 5] with timeWindow [2, 12] = [2, 5] which has length 3.0
        assertThat(perclos, is((3.0 + 5.0) / 10.0));
    }
}
