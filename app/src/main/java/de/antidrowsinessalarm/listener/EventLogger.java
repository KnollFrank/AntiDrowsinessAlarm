package de.antidrowsinessalarm.listener;

import android.util.Log;

import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.ConsecutiveUpdateEvents;
import de.antidrowsinessalarm.event.DrowsyEvent;

public class EventLogger {

    @Subscribe
    public void logConsecutiveUpdateEvents(ConsecutiveUpdateEvents event) {
        Log.d(this.getClass().getSimpleName(), "" + event);
    }

    @Subscribe
    public void logDrowsyEvent(DrowsyEvent event) {
        Log.d(this.getClass().getSimpleName(), "" + event);
    }
}
