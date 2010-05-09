package no.statnett.larm.core.web.service;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

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
