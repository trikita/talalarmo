package trikita.talalarmo.alarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import trikita.anvil.RenderableView;
import trikita.anvil.Anvil;
import static trikita.anvil.DSL.*;

import trikita.jedux.Action;
import trikita.talalarmo.Actions;
import trikita.talalarmo.App;
import trikita.talalarmo.ui.Theme;


public class AlarmActivity extends Activity {
    private PowerManager.WakeLock mWakeLock;
    private ValueAnimator mAnimator;
    private int bgColor;
    private String txt;
    private boolean canceled;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AlarmActivity");
        mWakeLock.acquire();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        // fill status bar with a theme dark color on post-Lollipop devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Theme.get(App.getState().settings().theme()).primaryDarkColor);
        }

        txt = "\ue857"; // alarm_off icon
        int colorFrom = Theme.get(App.getState().settings().theme()).backgroundColor;
        int colorTo = Theme.get(App.getState().settings().theme()).accentColor;
        bgColor = colorFrom;
        mAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        mAnimator.setDuration(2000); // millis
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bgColor = (int) animator.getAnimatedValue();
                Anvil.render();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                canceled = false;
                txt = "\ue913"; // touch_app icon
            }
            @Override
            public void onAnimationCancel(Animator animator) {
                canceled = true;
                txt = "\ue857"; // alarm_off icon
                bgColor = colorFrom;
                Anvil.render();
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!canceled) {
                    stopAlarm();
                }
            }
        });

        setContentView(new RenderableView(this) {
            public void view() {
                Theme.materialIcon(() -> {
                    size(FILL, FILL);
                    text(txt);
                    textColor(Theme.get(App.getState().settings().theme()).accentColor);
                    textSize(dip(128));
                    backgroundColor(bgColor);
                    onTouch((v, e) -> {
                        if(e.getAction() == MotionEvent.ACTION_DOWN){
                            mAnimator.start();
                            return true;
                        }
                        if(e.getAction() == MotionEvent.ACTION_UP){
                            mAnimator.cancel();
                            return true;
                        }
                        return false;
                    });
                });
            }
        });
    }

    @Override
    protected void onUserLeaveHint() {
        stopAlarm();
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        stopAlarm();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    private void stopAlarm() {
        App.dispatch(new Action<>(Actions.Alarm.DISMISS));
        finish();
    }
}
