package trikita.talalarmo.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import trikita.jedux.Action;
import trikita.talalarmo.Actions;
import trikita.talalarmo.App;
import trikita.talalarmo.MainActivity;

import static trikita.anvil.DSL.*;

public class SettingsLayout {
    public static void view() {
        linearLayout(() -> {
            orientation(LinearLayout.VERTICAL);

            header();

            checkBox(() -> {
                checked(App.getState().settings().vibrate());
                onCheckedChange((CompoundButton buttonView, boolean isChecked) -> {
                    App.dispatch(new Action<>(Actions.Settings.SET_VIBRATE, isChecked));
                });
            });

            checkBox(() -> {
                checked(App.getState().settings().ramping());
                onCheckedChange((CompoundButton buttonView, boolean isChecked) -> {
                    App.dispatch(new Action<>(Actions.Settings.SET_RAMPING, isChecked));
                });
            });

            button(() -> {
                text("Select ringtone...");
                onClick(v -> {
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(App.getState().settings().ringtone()));
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM | RingtoneManager.TYPE_RINGTONE);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Hey, ringtones!");
                    ((Activity) v.getContext()).startActivityForResult(intent, MainActivity.RINGTONE_REQUEST_CODE);
                });
            });
        });
    }

    private static void header() {
        linearLayout(() -> {
            size(FILL, WRAP);
            gravity(CENTER_VERTICAL);
            Theme.materialIcon(() -> {
                textColor(Theme.LIGHT.primaryTextColor);
                textSize(dip(32));
                padding(dip(15));
                text("\ue855"); // "alarm" icon
            });
            textView(() -> {
                size(WRAP, WRAP);
                weight(1f);
                typeface("fonts/Roboto-Light.ttf");
                textSize(dip(20));
                textColor(Theme.LIGHT.primaryTextColor);
                text("Settings");
            });
        });
    }
}
