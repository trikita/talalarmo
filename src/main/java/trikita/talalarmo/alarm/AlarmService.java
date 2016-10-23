package trikita.talalarmo.alarm;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Vibrator;

import trikita.talalarmo.App;

public class AlarmService extends Service {
    // Time period between two vibration events
    private final static int VIBRATE_DELAY_TIME = 2000;
    // Vibrate for 1000 milliseconds
    private final static int DURATION_OF_VIBRATION = 1000;
    // Increase alarm volume gradually every 600ms
    private final static int VOLUME_INCREASE_DELAY = 600;
    // Volume level increasing step
    private final static float VOLUME_INCREASE_STEP = 0.01f;
    // Max player volume level
    private final static float MAX_VOLUME = 1.0f;

    private MediaPlayer mPlayer;
    private Vibrator mVibrator;
    private float mVolumeLevel = 0;

    private Handler mHandler = new Handler();
    private Runnable mVibrationRunnable = new Runnable() {
        @Override
        public void run() {
            mVibrator.vibrate(DURATION_OF_VIBRATION);
            // Provide loop for vibration
            mHandler.postDelayed(mVibrationRunnable,
                    DURATION_OF_VIBRATION + VIBRATE_DELAY_TIME);
        }
    };

    private Runnable mVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            // increase volume level until reach max value
            if (mPlayer != null && mVolumeLevel < MAX_VOLUME) {
                mVolumeLevel += VOLUME_INCREASE_STEP;
                mPlayer.setVolume(mVolumeLevel, mVolumeLevel);
                // set next increase in 600ms
                mHandler.postDelayed(mVolumeRunnable, VOLUME_INCREASE_DELAY);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = (mp, what, extra) -> {
        mp.stop();
        mp.release();
        mHandler.removeCallbacksAndMessages(null);
        AlarmService.this.stopSelf();
        return true;
    };

    @Override
    public void onCreate() {
        HandlerThread ht = new HandlerThread("alarm_service");
        ht.start();
        mHandler = new Handler(ht.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startPlayer();
        // Start the activity where you can stop alarm
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName(this, AlarmActivity.class));
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private void startPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(mErrorListener);

        try {
            // add vibration to alarm alert if it is set
            if (App.getState().settings().vibrate()) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mHandler.post(mVibrationRunnable);
            }
            // Player setup is here
            String ringtone = App.getState().settings().ringtone();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ringtone.startsWith("content://media/external/")
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
            }
            mPlayer.setDataSource(this, Uri.parse(ringtone));
            mPlayer.setLooping(true);
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setVolume(mVolumeLevel, mVolumeLevel);
            mPlayer.prepare();
            mPlayer.start();

            if (App.getState().settings().ramping()) {
                mHandler.postDelayed(mVolumeRunnable, VOLUME_INCREASE_DELAY);
            } else {
                mPlayer.setVolume(MAX_VOLUME, MAX_VOLUME);
            }
        } catch (Exception e) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            stopSelf();
        }
    }
}
