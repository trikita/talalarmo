package trikita.talalarmo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import trikita.anvil.Anvil;
import trikita.anvil.RenderableView;
import trikita.promote.Promote;
import trikita.talalarmo.ui.AlarmLayout;
import trikita.talalarmo.ui.Theme;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        updateTheme();

        setContentView(new RenderableView(this) {
            public void view() {
                AlarmLayout.view();
            }
        });
    }

    public void onResume() {
        super.onResume();
        updateTheme();
        Anvil.render();
        Promote.after(7).days().every(7).days().rate(this);
        Promote.after(3).days().every(14).days().share(this,
                Promote.FACEBOOK_TWITTER,
                "https://github.com/trikita/talalarmo",
                "Talalarmo: elegant open-source alarm clock");
    }

    public void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void updateTheme() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (Theme.get(App.getState().settings().theme()).light) {
                setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
            } else {
                setTheme(android.R.style.Theme_Holo_NoActionBar);
            }
        } else {
            if (Theme.get(App.getState().settings().theme()).light) {
                setTheme(android.R.style.Theme_Material_Light_NoActionBar);
            } else {
                setTheme(android.R.style.Theme_Material_NoActionBar);
            }
        }

        // fill status bar with a theme dark color on post-Lollipop devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Theme.get(App.getState().settings().theme()).primaryDarkColor);
        }
    }

}
