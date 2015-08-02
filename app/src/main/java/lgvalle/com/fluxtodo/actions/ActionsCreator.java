package lgvalle.com.fluxtodo.actions;

import lgvalle.com.fluxtodo.dispatcher.Dispatcher;
import lgvalle.com.fluxtodo.model.Todo;

/**
 * Created by lgvalle on 02/08/15.
 */
public class ActionsCreator {

    private static ActionsCreator instance;
    final Dispatcher dispatcher;

    ActionsCreator(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static ActionsCreator get(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new ActionsCreator(dispatcher);
        }
        return instance;
    }

    public void create(String text) {
        dispatcher.dispatch(
                TodoActions.TODO_CREATE,
                TodoActions.KEY_TEXT, text
        );

    }

    public void destroy(long id) {
        dispatcher.dispatch(
                TodoActions.TODO_DESTROY,
                TodoActions.KEY_ID, id
        );
    }

    public void undoDestroy() {
        dispatcher.dispatch(
                TodoActions.TODO_UNDO_DESTROY
        );
    }

    public void toggleComplete(Todo todo) {
        long id = todo.getId();
        String actionType = todo.isComplete() ? TodoActions.TODO_UNDO_COMPLETE : TodoActions.TODO_COMPLETE;

        dispatcher.dispatch(
                actionType,
                TodoActions.KEY_ID, id
        );
    }

    public void toggleCompleteAll() {
        dispatcher.dispatch(TodoActions.TODO_TOGGLE_COMPLETE_ALL);
    }

    public void destroyCompleted() {
        dispatcher.dispatch(TodoActions.TODO_DESTROY_COMPLETED);
    }
}
