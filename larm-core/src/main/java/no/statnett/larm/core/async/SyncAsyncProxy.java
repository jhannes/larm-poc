package no.statnett.larm.core.async;

import java.lang.reflect.Method;

/**
 * Implement asynchronous interfaces through synchronous method calls.
 * Useful for testing.
 */
public class SyncAsyncProxy extends AsyncProxy {

    public SyncAsyncProxy(Object implementation, Class<?> targetInterface) {
        super(implementation, targetInterface);
    }

    public static<T> T createAsyncProxy(Class<T> targetInterface, Object implementation) {
        return createProxy(targetInterface, new SyncAsyncProxy(implementation, targetInterface));
    }

    @Override
    protected <T> void invokeAsync(Object implementation, Method targetMethod, Object[] args,
            AsyncCallback<T> callback) {
        execute(implementation, targetMethod, args, callback);
    }

}
