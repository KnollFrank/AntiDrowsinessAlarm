package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PERCLOSCalculatorTest {

    @Test
    public void shouldComputePERCLOS() {
        // Given
        PERCLOSCalculator perclosCalculator = new PERCLOSCalculator();

        // When
        double perclos = perclosCalculator.calculatePERCLOS(Arrays.asList(new SlowEyelidClosureEvent(100, 600), new SlowEyelidClosureEvent(1000, 550)), 2000);

        // Then
        assertThat(perclos, is((600.0 + 550.0) / 2000.0));
    }
}
