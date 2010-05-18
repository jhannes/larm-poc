package no.statnett.larm.core.web.service;

import java.io.OutputStream;
import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.AbstractHessianOutput;

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

    @Override
    public AbstractHessianOutput getHessianOutput(OutputStream os) {
        AbstractHessianOutput hessianOutput = super.getHessianOutput(os);
        hessianOutput.setSerializerFactory(new LarmHessianSerializerFactory());
        return hessianOutput;
    }
}
