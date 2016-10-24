package trikita.talalarmo;

import android.media.RingtoneManager;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Calendar;

import trikita.jedux.Action;
import trikita.jedux.Store;

@Value.Immutable
@Gson.TypeAdapters
public interface State {

    Settings settings();

    Alarm alarm();

    @Value.Immutable
    interface Settings {
        boolean vibrate();

        boolean snap();

        boolean ramping();

        String ringtone();

        int theme();
    }

    @Value.Immutable
    abstract class Alarm {
        public abstract boolean on();

        public abstract int minutes();

        public abstract int hours();

        public abstract boolean am();

        public Calendar nextAlarm() {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.AM_PM, (am() ? Calendar.AM : Calendar.PM));
            c.set(Calendar.HOUR, hours());
            c.set(Calendar.MINUTE, minutes());
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            if (System.currentTimeMillis() >= c.getTimeInMillis()) {
                c.add(Calendar.DATE, 1);
            }
            return c;
        }
    }

    class Default {
        public static State build() {
            return ImmutableState.builder()
                    .alarm(ImmutableAlarm.builder()
                            .on(false)
                            .am(false)
                            .hours(10)
                            .minutes(0)
                            .build())
                    .settings(ImmutableSettings.builder()
                            .ringtone(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())
                            .ramping(true)
                            .snap(true)
                            .vibrate(true)
                            .theme(0)
                            .build())
                    .build();
        }
    }

    class Reducer implements Store.Reducer<Action, State> {
        public State reduce(Action action, State currentState) {
            return ImmutableState.builder().from(currentState)
                    .alarm(reduceAlarm(action, currentState.alarm()))
                    .settings(reduceSettings(action, currentState.settings()))
                    .build();
        }

        State.Settings reduceSettings(Action action, State.Settings settings) {
            if (action.type instanceof Actions.Settings) {
                Actions.Settings type = (Actions.Settings) action.type;
                switch (type) {
                    case SET_RAMPING:
                        return ImmutableSettings.copyOf(settings).withRamping((Boolean) action.value);
                    case SET_VIBRATE:
                        return ImmutableSettings.copyOf(settings).withVibrate((Boolean) action.value);
                    case SET_SNAP:
                        return ImmutableSettings.copyOf(settings).withSnap((Boolean) action.value);
                    case SET_RINGTONE:
                        return ImmutableSettings.copyOf(settings).withRingtone((String) action.value);
                    case SET_THEME:
                        return ImmutableSettings.copyOf(settings).withTheme((Integer) action.value);
                }
            }
            return settings;
        }

        State.Alarm reduceAlarm(Action action, State.Alarm alarm) {
            if (action.type instanceof Actions.Alarm) {
                Actions.Alarm type = (Actions.Alarm) action.type;
                switch (type) {
                    case ON:
                        return ImmutableAlarm.copyOf(alarm).withOn(true);
                    case OFF:
                        return ImmutableAlarm.copyOf(alarm).withOn(false);
                    case SET_MINUTE:
                        return ImmutableAlarm.copyOf(alarm).withMinutes((Integer) action.value);
                    case SET_HOUR:
                        return ImmutableAlarm.copyOf(alarm).withHours((Integer) action.value);
                    case SET_AM_PM:
                        return ImmutableAlarm.copyOf(alarm).withAm((Boolean) action.value);
                }
            }
            return alarm;
        }
    }
}
