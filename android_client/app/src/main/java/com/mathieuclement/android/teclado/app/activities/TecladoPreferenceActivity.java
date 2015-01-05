package com.mathieuclement.android.teclado.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.mathieuclement.android.teclado.app.R;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

public class TecladoPreferenceActivity extends PreferenceActivity {
    // Preference names
    static final String PREF_HOST = "host";
    static final String PREF_PORT = "port";
    static final String PREF_VOL_PLUS_IS_NEXT_SLIDE = "volPlusIsNextSlide";
    static final String PREF_KEEP_DISPLAY_ON = "keepDisplayOn";

    // Preference objects
    private Preference hostPref;
    private Preference portPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setSubtitle("Settings");
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Preference.OnPreferenceChangeListener prefChangeListener = new OnPrefChangedListener();

        // Host preference
        hostPref = findPreference(PREF_HOST);
        hostPref.setSummary(sharedPreferences.getString(PREF_HOST, ""));
        hostPref.setOnPreferenceChangeListener(prefChangeListener);

        // Port preference
        portPref = findPreference(PREF_PORT);
        portPref.setSummary(sharedPreferences.getString(PREF_PORT, "12000"));
        portPref.setOnPreferenceChangeListener(prefChangeListener);

        // Other preferences
        findPreference(PREF_VOL_PLUS_IS_NEXT_SLIDE).setOnPreferenceChangeListener(prefChangeListener);
        findPreference(PREF_KEEP_DISPLAY_ON).setOnPreferenceChangeListener(prefChangeListener);
    }

    private class OnPrefChangedListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (newValue == null) return false; // invalid

            if (preference == hostPref) {
                String strValue = (String) newValue;
                // Check the address is compliant to standards
                if (strValue.length() > 1 && (DomainValidator.getInstance(false).isValid(strValue) ||
                        InetAddressValidator.getInstance().isValid(strValue))) {
                    hostPref.setSummary((CharSequence) newValue);
                    return true; // valid
                } else {
                    displayInvalidPrefMessage();
                    return false; // invalid
                }
            } else if (preference == portPref) {
                String strValue = (String) newValue;
                int intValue = Integer.parseInt(strValue);
                // Check port is valid
                if (intValue > 0 && intValue <= 65536) { // max allowed IP port
                    portPref.setSummary(strValue);
                    return true; // valid
                } else {
                    displayInvalidPrefMessage();
                    return false; // invalid
                }
            }

            // Ignore other preference changes
            // Also, they are valid.
            return true;
        }
    }

    private void displayInvalidPrefMessage() {
        Toast.makeText(this, "Setting was not valid, set to previous value.", Toast.LENGTH_LONG).show();
    }
}
