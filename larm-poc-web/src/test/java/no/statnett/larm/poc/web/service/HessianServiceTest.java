package no.statnett.larm.poc.web.service;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import no.statnett.larm.core.web.service.LarmHessianProxyFactory;
import no.statnett.larm.poc.web.WebTest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.server.HessianSkeleton;

public class HessianServiceTest extends WebTest {
    private static ServiceInterface service = mock(ServiceInterface.class);
    private static String applicationUrl;

    public interface ServiceInterface {

        String doIt(String arg);

    }

    @Test
    public void shouldAccessRemoteServiceOverHttp() throws Exception {
        when(service.doIt(anyString())).thenReturn("foo");

        ServiceInterface serviceOnClient = LarmHessianProxyFactory.createProxy(ServiceInterface.class, applicationUrl + "service/repositoryService");
        assertThat(serviceOnClient.doIt("bar")).isEqualTo("foo");

        verify(service).doIt("bar");
    }

    @Test
    public void shouldForwardUncheckedExceptions() throws Exception {
        String message = "Some error has occurred 325254231";
        when(service.doIt(anyString())).thenThrow(new IllegalArgumentException(message));
        ServiceInterface serviceOnClient = LarmHessianProxyFactory.createProxy(ServiceInterface.class, applicationUrl + "service/repositoryService");

        try {
            serviceOnClient.doIt("something");
        } catch (UndeclaredThrowableException e) {
            // TODO: This is not what we want. Better understanding of Hessian may give us the correct exception
            assertThat(e.getCause()).isInstanceOf(InvocationTargetException.class);
            assertThat(e.getCause().getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getCause().getMessage()).isEqualTo(message);
        }
    }

    @Test
    public void shouldThrowExceptionOnUnknownService() throws Exception {
        ServiceInterface serviceOnClient = LarmHessianProxyFactory.createProxy(ServiceInterface.class,
                applicationUrl + "service/notAService");
        try {
            serviceOnClient.doIt("bar");
        } catch (HessianException e) {
            assertThat(e.getCause()).isInstanceOf(FileNotFoundException.class);
        }
    }

    @BeforeClass
    public static void startServer() throws Exception {
        Server server = new Server(0);
        WebAppContext context = new WebAppContext("src/main/webapp", "/");
        context.setAttribute("repositoryService", new HessianSkeleton(service, ServiceInterface.class));
        server.addHandler(context);
        server.start();
        int serverPort = server.getConnectors()[0].getLocalPort();
        applicationUrl = "http://localhost:" + serverPort + "/";
    }

    @Before
    public void resetMock() {
        reset(service);
    }
}
