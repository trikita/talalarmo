package trikita.talalarmo;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

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
            case "repeat_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_REPEAT, prefs.getBoolean(key, true)));
                break;
            case "snap_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_SNAP, prefs.getBoolean(key, true)));
                break;
            case "offset_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_OFFSET, getIntSetting(prefs, key, 30)));
                break;
            case "vibration_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_VIBRATE, prefs.getBoolean(key, false)));
                break;
            case "ramping_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_RAMPING, prefs.getBoolean(key, true)));
                break;
            case "ringtone_setting":
                String s = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && prefs.getString(key, s).startsWith("content://media/external/")
                        && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                App.dispatch(new Action<>(Actions.Settings.SET_RINGTONE, prefs.getString(key, s)));
                break;
            case "theme_setting":
                App.dispatch(new Action<>(Actions.Settings.SET_THEME, getIntSetting(prefs, key, 0)));
                recreate();
                break;
        }
    }

    private int getIntSetting(SharedPreferences prefs, String key, int def) {
        int value = def;
        try {
            value = Integer.parseInt(prefs.getString(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            Log.w("Talalarmo", e.toString());
        }
        return value;
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
