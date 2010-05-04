package no.statnett.larm.core.async;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

/**
 * Implements asynchronous calls using the SwingWorker facility.
 */
public class SwingWorkerAsyncProxy extends AsyncProxy {

    public SwingWorkerAsyncProxy(Object implementation, Class<?> targetInterface) {
        super(implementation, targetInterface);
    }

    public static<T> T createAsyncProxy(Class<T> targetInterface, Object implementation) {
        return createProxy(targetInterface, new SwingWorkerAsyncProxy(implementation, targetInterface));
    }

    @Override
    protected <T> void invokeAsync(final Object target, final Method method, final Object[] args,
            final AsyncCallback<T> callback) {
        SwingWorker<T, Object> worker = new SwingWorker<T, Object>() {
            @Override
            @SuppressWarnings("unchecked")
            protected T doInBackground() throws Exception {
                return (T)method.invoke(target, args);
            }

            @Override
            protected void done() {
                T result;
                try {
                    result = get();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Should never happen - right?");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    while (cause.getCause() != null) {
                    	cause = cause.getCause();
                    }
                    callback.onFailure(cause);
                    return;
                }
                callback.onSuccess(result);
            }
        };
        worker.execute();
    }

}
