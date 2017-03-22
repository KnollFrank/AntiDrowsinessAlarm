package de.antidrowsinessalarm;

import android.content.SharedPreferences;

import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

class SharedPreferencesTestFactory {

    public static SharedPreferences createSharedPreferences() {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        when(sharedPreferences.getString(eq("drowsyThreshold"), anyString())).thenReturn("0.15");
        when(sharedPreferences.getString(eq("slowEyelidClosureMinDuration"), anyString())).thenReturn("500");
        when(sharedPreferences.getString(eq("eyeOpenProbabilityThreshold"), anyString())).thenReturn("0.5");
        when(sharedPreferences.getString(eq("likelyDrowsyThreshold"), anyString())).thenReturn("0.08");
        return sharedPreferences;
    }
}
