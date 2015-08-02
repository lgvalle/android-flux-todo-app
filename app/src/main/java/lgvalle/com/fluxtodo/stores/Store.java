package lgvalle.com.fluxtodo.stores;

import lgvalle.com.fluxtodo.actions.Action;
import lgvalle.com.fluxtodo.dispatcher.Dispatcher;

/**
 * Created by lgvalle on 02/08/15.
 */
public abstract class Store {

    final Dispatcher dispatcher;

    protected Store(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    void emitStoreChange() {
        dispatcher.emitChange(changeEvent());
    }

    abstract StoreChangeEvent changeEvent();
    public abstract void onAction(Action action);

    public interface StoreChangeEvent {}
}
