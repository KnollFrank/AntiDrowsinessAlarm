package de.antidrowsinessalarm;

import org.joda.time.Instant;

public interface Clock {

    Instant now();
}
