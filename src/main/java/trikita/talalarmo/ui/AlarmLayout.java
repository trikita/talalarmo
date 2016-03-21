package trikita.talalarmo.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import trikita.anvil.Anvil;
import trikita.anvil.RenderableView;
import trikita.jedux.Action;
import trikita.talalarmo.Actions;
import trikita.talalarmo.App;
import trikita.talalarmo.R;

import static trikita.anvil.DSL.*;

public class AlarmLayout {
    public static void view() {
        backgroundColor(Theme.LIGHT.backgroundColor);
        linearLayout(() -> {
            orientation(LinearLayout.VERTICAL);
            header();
            frameLayout(() -> {
                size(FILL, 0);
                weight(1f);
                if (App.getState().alarm().on()) {
                    alarmOnLayout();
                } else {
                    alarmOffLayout();
                }
            });
            bottomBar();
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
                text(R.string.app_name);
            });
        });
    }

    private static void alarmOffLayout() {
        textView(() -> {
            size(FILL, FILL);
            padding(dip(20));
            gravity(LEFT | CENTER_VERTICAL);
            typeface("fonts/Roboto-Light.ttf");
            textSize(dip(32));
            textColor(Theme.LIGHT.secondaryTextColor);
            text(R.string.tv_start_alarm_text);
            onClick(v -> App.dispatch(new Action<>(Actions.Alarm.ON)));
        });
    }

    private static void alarmOnLayout() {
        frameLayout(() -> {
            size(FILL, FILL);
            margin(dip(8));
            int w = Anvil.currentView().getWidth();
            int h = Anvil.currentView().getHeight();
            if (h == 0 || w == 0) {
                Anvil.currentView().post(Anvil::render);
            }

            int hourCircleSize = (int) (w * 1.1f * 0.62f);
            int minuteCircleSize = (int) (hourCircleSize * 0.62f);
            int amPmWidth = (int) (hourCircleSize * 0.62f * 0.62f);

            frameLayout(() -> {
                size(hourCircleSize, hourCircleSize);
                x(w/2 - hourCircleSize * 0.21f - hourCircleSize/2);
                y(h/2 + hourCircleSize * 0.19f - hourCircleSize/2);
                gravity(CENTER);
                v(ClockView.class, () -> {
                    size(FILL, FILL);
                    progress(App.getState().alarm().hours());
                    max(12);
                    onSeekBarChange((v, progress, fromUser) -> {
                        if (fromUser) {
                            App.dispatch(new Action<>(Actions.Alarm.SET_HOUR, progress));
                        }
                    });
                    Anvil.currentView().invalidate();
                });
                textView(() -> {
                    size(WRAP, WRAP);
                    int hours = App.getState().alarm().hours();
                    if (hours == 0) {
                        text("12");
                    } else {
                        text(String.format("%02d", hours));
                    }
                    layoutGravity(CENTER);
                    typeface("fonts/Roboto-Light.ttf");
                    textSize(hourCircleSize * 0.3f);
                    textColor(Theme.LIGHT.secondaryTextColor);
                });
            });

            frameLayout(() -> {
                size(minuteCircleSize, minuteCircleSize);
                x(w/2 - hourCircleSize * 0.25f + minuteCircleSize/2);
                y(h/2 + hourCircleSize * 0.05f - hourCircleSize/2 - minuteCircleSize/2);
                gravity(CENTER);
                v(ClockView.class, () -> {
                    size(FILL, FILL);
                    progress(App.getState().alarm().minutes());
                    max(60);
                    onSeekBarChange((v, progress, fromUser) -> {
                        if (fromUser) {
                            App.dispatch(new Action<>(Actions.Alarm.SET_MINUTE, progress));
                        }
                    });
                    Anvil.currentView().invalidate();
                });
                textView(() -> {
                    size(WRAP, WRAP);
                    text(String.format("%02d", App.getState().alarm().minutes()));
                    layoutGravity(CENTER);
                    typeface("fonts/Roboto-Light.ttf");
                    textSize(minuteCircleSize * 0.3f);
                    textColor(Theme.LIGHT.secondaryTextColor);
                });
            });

            v(AmPmSwitch.class, () -> {
                size(amPmWidth, (int) (amPmWidth/1.5f));
                x(w/2 - hourCircleSize * 0.21f - amPmWidth*3/4);
                y(h/2 + hourCircleSize * 0.05f - hourCircleSize/2 - amPmWidth/1.5f/2);
                checked(App.getState().alarm().am());
                onCheckedChange((CompoundButton buttonView, boolean isChecked) -> {
                    App.dispatch(new Action<>(Actions.Alarm.SET_AM_PM, isChecked));
                });
            });
        });
    }

    private static void bottomBar() {
        linearLayout(() -> {
            size(FILL, dip(62));
            backgroundColor(Theme.LIGHT.backgroundTranscluentColor);

            Theme.materialIcon(() -> {
                text("\ue857"); // ALARM OFF
                textSize(dip(32));
                textColor(Theme.LIGHT.secondaryTextColor);
                padding(dip(15));
                visibility(App.getState().alarm().on());
                onClick(v -> App.dispatch(new Action<>(Actions.Alarm.OFF)));
            });

            textView(() -> {
                size(0, FILL);
                weight(1f);
                margin(dip(10), 0);
                typeface("fonts/Roboto-Light.ttf");
                textSize(dip(20));
                textColor(Theme.LIGHT.primaryTextColor);
                gravity(CENTER | CENTER_VERTICAL);
                if (App.getState().alarm().on()) {
                    long t = App.getState().alarm().nextAlarm().getTimeInMillis() - System.currentTimeMillis() - 1;
                    t = t / 60 / 1000;
                    int m = (int) (t % 60);
                    t = t / 60;
                    String alarmTime = "";
                    if (t > 0) {
                        if (t == 1) {
                            alarmTime = alarmTime + "1 hour";
                        } else {
                            alarmTime = alarmTime + t + " hours";
                        }
                    }
                    if (m > 0) {
                        if (t > 0) {
                            alarmTime = alarmTime + " ";
                        }
                        if (m == 1) {
                            alarmTime = alarmTime + "1 minute";
                        } else {
                            alarmTime = alarmTime + m + " minutes";
                        }
                    }
                    if (t != 0 || m != 0) {
                        alarmTime = alarmTime + " from ";
                    }
                    text("Alarm set for " + alarmTime + "now");
                } else {
                    text("");
                }
            });

            Theme.materialIcon(() -> {
                text("\ue5d4"); // "more vert"
                textSize(dip(32));
                textColor(Theme.LIGHT.secondaryTextColor);
                padding(dip(15));
                onClick(v -> showSettingsMenu(v));
            });
        });
    }

    private static void showSettingsMenu(View v) {
        final PopupWindow popupWindow = new PopupWindow(new RenderableView(v.getContext()) {
            public void view() {
                linearLayout(() -> {
                    size(dip(200), FILL);
                    minHeight(dip(110));
                    margin(dip(6));
                    orientation(LinearLayout.VERTICAL);
                    backgroundResource(Theme.LIGHT.popupMenuBackground);

                    button(() -> {
                        size(FILL, 0);
                        weight(0.5f);
                        typeface("fonts/Roboto-Light.ttf");
                        text(R.string.om_settings_title);
                        textColor(Theme.LIGHT.primaryTextColor);
                        textSize(dip(18));
                        gravity(LEFT | CENTER_VERTICAL);
                        padding(dip(25), 0, dip(20), 0);
                        backgroundResource(Theme.LIGHT.popupMenuItemSelector);
                        //TODO: dismiss() on click
                        onClick(v -> App.dispatch(new Action<>(Actions.Navigation.SETTINGS)));
                    });
                    v(View.class, () -> {
                        size(FILL, 1);
                        backgroundColor(Theme.LIGHT.dividerColor);
                        margin(dip(2), 0);
                    });
                    button(() -> {
                        size(FILL, 0);
                        weight(0.5f);
                        typeface("fonts/Roboto-Light.ttf");
                        text(R.string.om_feedback_title);
                        textColor(Theme.LIGHT.primaryTextColor);
                        textSize(dip(18));
                        gravity(LEFT | CENTER_VERTICAL);
                        padding(dip(25), 0, dip(20), 0);
                        backgroundResource(Theme.LIGHT.popupMenuItemSelector);
                        //TODO: dismiss() on click
                        onClick(v -> {
                            Context c = getContext();
                            Intent intent = new Intent(Intent.ACTION_SENDTO,
                                    Uri.fromParts("mailto", "adm.trikita@gmail.com", null));
                            intent.putExtra(Intent.EXTRA_SUBJECT, c.getString(R.string.feedback_email_subject));
                            v.getContext().startActivity(Intent.createChooser(intent, c.getString(R.string.dlg_email_chooser_title)));
                        });
                    });
                });
            }
        }, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
        // height of the popup window is ~120dp
        popupWindow.showAsDropDown(v, 0, (int) (-120 * metrics.density));
    }
}
