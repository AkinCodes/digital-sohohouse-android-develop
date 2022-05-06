package com.sohohouse.seven.base.mvpimplementation;

import android.content.res.Configuration;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sohohouse.seven.base.BasePresenter;


public interface Presenter<V extends ViewController> {
    @MainThread
    void attach(V view);

    /**
     * This is called when the view has been recreated regardless the value returned on {@link #isRetained()}
     */
    @MainThread
    void onRecreated();

    @SuppressWarnings("unused")
    V getView();

    @MainThread
    void detach();

    /**
     * <pre>
     * If the view is available then the task will be execute right away but if not then it will
     *
     * wait until {@link com.sohohouse.seven.base.BasePresenter#onAttach(ViewController, boolean, boolean)}. One use case will be as below. To do the same task
     *
     * when the view is not available, I would have to save the state and then implement the code
     *
     * on {@link com.sohohouse.seven.base.BasePresenter#onAttach(ViewController, boolean, boolean)}}}
     *
     *     <code>
     *
     *     //before
     *     public static class ExamplePresenter {
     *
     *         private State mState;
     *
     *         public void onAttach(boolean isFirst) {
     *             switch (mState) {
     *                 case SUCCESS:
     *                     getView().showSuccess();
     *             }
     *         }
     *
     *         public void doSomeTask() {
     *             executeBackgroundTask(new Listener() {
     *                 public void onCallback() {
     *
     *                     View view = getView();
     *                     if (view != null) {
     *                         view.showSuccess();
     *                     } else {
     *                         mState = SUCCESS;
     *                     }
     *                 }
     *             });
     *         }
     *     }
     *
     *     //after
     *     public static class ExamplePresenter {
     *         public void doSomeTask() {
     *             executeBackgroundTask(new Listener() {
     *                 public void onCallback() {
     *                     executeWhenAvailable(view -> view.showSuccess());
     *                 }
     *             });
     *         }
     *     }
     *
     *    </code>
     * </pre>
     *
     * @param task task to execute
     */
    @SuppressWarnings("unused")
    void executeWhenAvailable(OnAttachQueueTask<V> task);

    @SuppressWarnings("unused")
    void executeWhenAvailable(OnAttachQueueTask<V> task, long delayMs);

    /**
     * See {@link #executeIfAvailableThrottle(Task, long)}
     *
     * @param task task to execute
     */
    @SuppressWarnings("unused")
    void executeIfAvailableThrottle(Task<V> task);

    /**
     * <pre>
     * This task will be ONLY executed if the view is available at the moment and if the time when
     *
     * it successfully executed the last task exceeds the the throttled value from {@link #getThrottleMs()}.
     *
     * This will not queue the task. This will help get rid of the boiler code as below
     *
     * <code>
     *
     *     //before
     *     public class ExamplePresenter {
     *
     *         public void doSomeTask() {
     *             executeBackgroundTask(new Listener() {
     *                 public void onCallback() {
     *                     View view = getView();
     *                     if (view != null) {
     *                         view.showSuccess();
     *                     }
     *                 }
     *             });
     *         }
     *     }
     *
     *     //after
     *     public class ExamplePresenter {
     *
     *         public void doSomeTask() {
     *             executeBackgroundTask(new Listener() {
     *                 public void onCallback() {
     *                     executeIfAvailable(view -> view.showSuccess());
     *                 }
     *             });
     *         }
     *     }
     *
     * </code>
     * </pre>
     **/
    @SuppressWarnings("unused")
    void executeIfAvailableThrottle(Task<V> task, long throttleMs);

    /**
     * This will run without throttle. See {@link #executeIfAvailableThrottle(Task, long)}
     *
     * @param task task to execute
     */
    @SuppressWarnings("unused")
    void executeIfAvailable(Task<V> task);


    /**
     * This value is a cool down time it requires to wait before executing next task
     * <p>
     * when executing {@link #executeIfAvailableThrottle(Task, long)} or {@link #executeIfAvailableThrottle(Task)}.
     *
     * @return the cool down time in milliseconds
     */
    @SuppressWarnings("unused")
    long getThrottleMs();

    /**
     * When this returns {@code true}, this will be retained even when the view gets destroyed and recreated.
     * ie. orientation change or {@link android.app.Activity#onConfigurationChanged(Configuration)}
     *
     * @return true if this will be retained, false otherwise
     */
    @SuppressWarnings("SameReturnValue")
    boolean isRetained();

    /**
     * This callback will be different depending on the value that's returned by {@link #isRetained()}.
     * When it's {@code true} it will not be called when the view is being recreated and
     * when it's {code false} it will be called any time the view calls
     * {@link android.app.Activity#onDestroy} or {@link Fragment#onDestroy()}.
     */
    @SuppressWarnings("EmptyMethod")
    void onDestroy();

    /**
     * Listens for call back in {@link BasePresenter#onAttach(ViewController, boolean, boolean)}}.
     * <p>
     * This class is used with {@link #executeWhenAvailable(OnAttachQueueTask)} to queue a task
     * <p>
     * if the view is currently unavailable and execute it when the view becomes available again
     *
     * @param <V>
     */
    interface OnAttachQueueTask<V extends ViewController> {

        @MainThread
        void onAttach(@NonNull V view, boolean isFirstAttach, boolean isRecreated);
    }

    /**
     * This class is used with {@link #executeIfAvailable(Task)} and it only executes if the view is available.
     *
     * @param <V>
     */
    interface Task<V extends ViewController> {
        @MainThread
        void execute(V view);
    }

}
