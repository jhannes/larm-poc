package no.statkraft.larm.core.web.service;

import com.caucho.hessian.client.HessianProxyFactory;

import java.net.MalformedURLException;

public class LarmHessianProxyFactory extends HessianProxyFactory {

    @SuppressWarnings("unchecked")
    public static<T> T createProxy(Class<T> serviceInterface, String url) {
        try {
            LarmHessianProxyFactory factory = new LarmHessianProxyFactory();
            return (T) factory.create(serviceInterface, url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
