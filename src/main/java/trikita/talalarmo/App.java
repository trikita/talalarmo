package trikita.talalarmo;

import android.app.Application;

import trikita.anvil.Anvil;
import trikita.jedux.Action;
import trikita.jedux.Logger;
import trikita.jedux.Store;
import trikita.talalarmo.alarm.AlarmController;
import trikita.talalarmo.alarm.PersistanceController;

public class App extends Application {

    private static App instance;

    private Store<Action, State> store;

    public static State dispatch(Action action) {
        return instance.store.dispatch(action);
    }

    public static State getState() {
        return instance.store.getState();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.instance = this;

        PersistanceController persistanceController = new PersistanceController(this);
        State initialState = persistanceController.getSavedState();
        if (initialState == null) {
            initialState = State.Default.build();
        }

        this.store = new Store<>(new State.Reducer(),
                initialState,
                new Logger<>("Talalarmo"),
                persistanceController,
                new AlarmController(this));

        this.store.subscribe(Anvil::render);
    }
}
