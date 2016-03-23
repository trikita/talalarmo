package trikita.talalarmo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import trikita.anvil.Anvil;
import trikita.anvil.RenderableView;
import trikita.talalarmo.ui.AlarmLayout;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        } else {
            setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        }
        setContentView(new RenderableView(this) {
            public void view() {
                AlarmLayout.view();
            }
        });
    }

    public void onBackPressed() {
        App.dispatch(new Action<>(Actions.Navigation.BACK));
        if (App.getState().navigation().exit()) {
            App.dispatch(new Action<>(Actions.Navigation.EXIT));
            super.onBackPressed();
        }
    }

    public void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }
        }
    }
}
