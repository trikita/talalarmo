package trikita.talalarmo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import trikita.jedux.Action;

public class SettingsActivity extends Activity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (App.getState().settings().theme() == 0) {
                setTheme(android.R.style.Theme_Holo_Light);
            } else {
                setTheme(android.R.style.Theme_Holo);
            }
        } else {
            if (App.getState().settings().theme() == 0) {
                setTheme(android.R.style.Theme_Material_Light);
            } else {
                setTheme(android.R.style.Theme_Material);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case "vibration_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_VIBRATE, prefs.getBoolean(key, false)));
                break;
            case "ramping_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_RAMPING, prefs.getBoolean(key, true)));
                break;
            case "snap_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_SNAP, prefs.getBoolean(key, true)));
                break;
            case "ringtone_setting":
                String s = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
                App.dispatch(new Action<>(Actions.Settings.SET_RINGTONE, prefs.getString(key, s)));
                break;
            case "theme_setting":
                int themeIndex = 0;
                try {
                    themeIndex = Integer.valueOf(prefs.getString("theme_setting", "0"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                App.dispatch(new Action<>(Actions.Settings.SET_THEME, themeIndex));
                break;
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
