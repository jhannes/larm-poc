package no.statnett.larm.core.async;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AsyncProxyTest {

    public interface SyncInterface {

        void foo(int i);

        long transform(String s, int i);

    }

    public interface AsyncInterface {

        void foo(int i, AsyncCallback<Void> callback);

        void transform(String s, int i, AsyncCallback<Long> callback);

    }

    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
        asyncProxy = SyncAsyncProxy.createAsyncProxy(AsyncInterface.class, mockTarget);
    }


    @Mock private SyncInterface mockTarget;

    @Mock private AsyncCallback<Long> mockLongCallback;

    @Mock private AsyncCallback<Void> mockVoidCallback;

    private AsyncInterface asyncProxy;


    @Test
    public void thatSuccessfulCallsArePerformed() throws Exception {
        Mockito.when(mockTarget.transform("12", 9)).thenReturn(1234L);
        asyncProxy.transform("12", 9, mockLongCallback);

        Mockito.verify(mockTarget).transform("12", 9);
        Mockito.verify(mockLongCallback).onSuccess(1234L);
    }

    @Test
    public void exceptionsAreSent() throws Exception {
        RuntimeException expectedException = new RuntimeException("fdgsnsl n");

        Mockito.doThrow(expectedException).when(mockTarget).foo(123);
        asyncProxy.foo(123, mockVoidCallback);

        Mockito.verify(mockVoidCallback).onFailure(expectedException);
    }

    @Test
    public void asyncImplementation() throws Exception {
        Mockito.when(mockTarget.transform("13", 12)).thenReturn(123L);
        AsyncInterface asynchProxy = AsyncProxy.createAsyncProxy(AsyncInterface.class, mockTarget);

        asynchProxy.transform("13", 12, mockLongCallback);

        Thread.sleep(10);
        Mockito.verify(mockLongCallback).onSuccess(123L);
    }

    @Test
    public void swingImplementation() throws Exception {
        Mockito.when(mockTarget.transform("13", 12)).thenReturn(123L);
        AsyncInterface asynchProxy = SwingWorkerAsyncProxy.createAsyncProxy(AsyncInterface.class, mockTarget);

        asynchProxy.transform("13", 12, mockLongCallback);

        synchronized (mockLongCallback) {
            mockLongCallback.wait(1000);
        }
        Mockito.verify(mockLongCallback).onSuccess(123L);
    }


    interface AsyncInterfaceWithExtraMethod extends AsyncInterface {
        void bar(int i, AsyncCallback<Void> callback);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateAsyncInterfaceWithExtraMethods() throws Exception {
        SyncAsyncProxy.createAsyncProxy(AsyncInterfaceWithExtraMethod.class, mockTarget);
    }

    interface AsyncInterfaceWithWrongParams {
        void foo(int i, int j, AsyncCallback<Void> callback);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateAsyncInterfaceWithWrongParameters() throws Exception {
        SyncAsyncProxy.createAsyncProxy(AsyncInterfaceWithWrongParams.class, mockTarget);
    }

    interface AsyncInterfaceWithNonVoidReturnTypes {
        int foo(int i, AsyncCallback<Void> callback);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateProxyForInterfaceWithNonVoidReturnTypes() {
        SyncAsyncProxy.createAsyncProxy(AsyncInterfaceWithNonVoidReturnTypes.class, mockTarget);
    }


}
