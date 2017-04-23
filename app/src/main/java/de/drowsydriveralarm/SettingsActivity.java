package de.drowsydriveralarm;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.pavelsikun.seekbarpreference.SeekBarPreference;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(final Preference preference, final Object value) {
            final String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                final ListPreference listPreference = (ListPreference) preference;
                final int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    final Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        final String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(final Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(final Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        final ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(final List<Header> target) {
        this.loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(final String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.pref_general);
            this.setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(this.findPreference("drowsyThreshold"));
            bindPreferenceSummaryToValue(this.findPreference("slowEyelidClosureMinDuration"));
            bindPreferenceSummaryToValue(this.findPreference("likelyDrowsyThreshold"));
            bindPreferenceSummaryToValue(this.findPreference("timeWindow"));

            final Preference resetButton = this.findPreference("reset");
            resetButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    SeekBarPreference eyeOpenProbabilityThresholdSeekBar = (SeekBarPreference) GeneralPreferenceFragment.this.findPreference("eyeOpenProbabilityThreshold");
                    eyeOpenProbabilityThresholdSeekBar.setCurrentValue(50);
                    this.clearPreferences(preferences, preference.getContext());
                    this.updatePreferenceSummaries(preferences);
                    return true;
                }

                private void clearPreferences(final SharedPreferences preferences, final Context context) {
                    preferences.edit().clear().commit();
                    PreferenceManager.setDefaultValues(context, R.xml.pref_general, true);
                }

                private void updatePreferenceSummaries(final SharedPreferences preferences) {
                    this.updatePreferenceSummary(preferences, "drowsyThreshold");
                    this.updatePreferenceSummary(preferences, "slowEyelidClosureMinDuration");
                    this.updatePreferenceSummary(preferences, "eyeOpenProbabilityThreshold");
                    this.updatePreferenceSummary(preferences, "likelyDrowsyThreshold");
                    this.updatePreferenceSummary(preferences, "timeWindow");
                }

                private void updatePreferenceSummary(final SharedPreferences preferences, final String key) {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(
                            GeneralPreferenceFragment.this.findPreference(key),
                            preferences.getString(key, ""));
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item) {
            final int id = item.getItemId();
            if (id == android.R.id.home) {
                this.startActivity(new Intent(this.getActivity(), FaceTrackerActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
