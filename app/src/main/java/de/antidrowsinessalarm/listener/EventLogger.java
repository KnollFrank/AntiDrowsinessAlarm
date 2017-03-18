package de.antidrowsinessalarm.listener;

import android.util.Log;

import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.ConsecutiveUpdateEvents;
import de.antidrowsinessalarm.event.Event;
import de.antidrowsinessalarm.event.UpdateEvent;

public class EventLogger {

    @Subscribe
    public void logEvent(Event event) {
        if (event instanceof ConsecutiveUpdateEvents || event instanceof UpdateEvent) {
            return;
        }

        Log.d(this.getClass().getSimpleName(), "" + event);
    }
}
