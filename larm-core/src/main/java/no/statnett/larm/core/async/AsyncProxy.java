package no.statnett.larm.core.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows for an asynchronous variation of any interface. Each method
 * on the asynchronous object has a corresponding method on a
 * synchronous interface. When calling the asynchronous interface
 * the invocation happens in a separate thread and the results
 * or any exception is passed to a callback object. Implement
 * invokeAsync() in subclasses for more specific implementation of
 * the asynchronous mechanism.
 */
public class AsyncProxy implements InvocationHandler {

    public static<T> T createAsyncProxy(Class<T> asyncInterface, Object implementation) {
        return createProxy(asyncInterface, new AsyncProxy(implementation, asyncInterface));
    }

    private Class<?> targetClass;
    private final Object implementation;

    private Map<Method,Method> syncMethods = new HashMap<Method, Method>();

    public AsyncProxy(Object implementation, Class<?> asyncInterface) {
        this.implementation = implementation;
        this.targetClass = implementation.getClass();

        Collection<Method> missingMethods = new ArrayList<Method>();
        Collection<Method> invalidMethods = new ArrayList<Method>();
        for (Method asyncMethod : asyncInterface.getMethods()) {
            if (asyncMethod.getReturnType() != Void.TYPE) {
                invalidMethods.add(asyncMethod);
                continue;
            }

            /*
             * TODO: Tests for these
             */
            Class<?>[] paramTypes = asyncMethod.getParameterTypes();
            if (paramTypes.length == 0) {
                invalidMethods.add(asyncMethod);
                continue;
            } else if (paramTypes[paramTypes.length-1] != AsyncCallback.class) {
                invalidMethods.add(asyncMethod);
                continue;
            }

            Class<?>[] params = Arrays.copyOf(paramTypes, paramTypes.length-1);

            try {
                Method syncMethod = targetClass.getMethod(asyncMethod.getName(), params);
                syncMethods.put(asyncMethod, syncMethod);
            } catch (NoSuchMethodException e) {
                missingMethods.add(asyncMethod);
            }
        }
        if (!invalidMethods.isEmpty()) {
            throw new IllegalArgumentException(asyncInterface + " has non-no.statnett.larm.core.async methods " + invalidMethods);
        }
        if (!missingMethods.isEmpty()) {
            throw new IllegalArgumentException(targetClass + " is missing target methods for " + missingMethods);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(proxy, args);
        }

        Method targetMethod = syncMethods.get(method);
        AsyncCallback<?> callback = (AsyncCallback<?>) args[args.length-1];
        Object[] targetArgs = Arrays.copyOf(args, args.length-1);

        invokeAsync(implementation, targetMethod, targetArgs, callback);

        return null;
    }

    /**
     * Override this method for specific implementation of asynchronous mechanisms
     */
    protected<T> void invokeAsync(final Object target, final Method method, final Object[] args, final AsyncCallback<T> callback) {
        new Thread(new Runnable() {
            public void run() {
                execute(target, method, args, callback);
            }
        }).start();
    }

    /**
     * May be called from an implementing subclass in the asynchronous thread
     */
    @SuppressWarnings("unchecked")
    final protected <T> void execute(Object implementation, Method targetMethod, Object[] args, AsyncCallback<T> callback) {
        T result;
        try {
            result = (T) targetMethod.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            while (cause.getCause() != null && cause.getCause() != cause)
                cause = cause.getCause();
            callback.onFailure(cause);
            return;
        } catch (Throwable e) {
            callback.onFailure(e);
            return;
        }
        callback.onSuccess(result);
    }

    @SuppressWarnings("unchecked")
    protected static <T> T createProxy(Class<T> targetInterface, AsyncProxy proxy) {
        return (T) Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[] { targetInterface }, proxy);
    }


}
