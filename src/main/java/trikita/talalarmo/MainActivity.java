package trikita.talalarmo;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import trikita.anvil.Anvil;
import trikita.anvil.RenderableView;
import trikita.jedux.Action;
import trikita.jedux.Logger;
import trikita.jedux.Store;
import trikita.talalarmo.ui.AlarmLayout;
import trikita.talalarmo.ui.SettingsLayout;

public class MainActivity extends Activity {

    public static final int RINGTONE_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(new RenderableView(this) {
            public void view() {
                if (App.getState().navigation().settingsScreen()) {
                    SettingsLayout.view();
                } else {
                    AlarmLayout.view();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        App.dispatch(new Action<>(Actions.Navigation.BACK));
        if (App.getState().navigation().exit()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_REQUEST_CODE && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            App.dispatch(new Action<>(Actions.Settings.SET_RINGTONE, uri.toString()));
        }
    }
}
