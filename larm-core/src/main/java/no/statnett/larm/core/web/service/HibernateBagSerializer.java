package no.statnett.larm.core.web.service;

import java.io.IOException;
import java.util.ArrayList;

import org.hibernate.collection.PersistentBag;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.CollectionSerializer;
import com.caucho.hessian.io.Serializer;

public class HibernateBagSerializer implements Serializer {

    private CollectionSerializer javaSerializer = new CollectionSerializer();


    @SuppressWarnings("unchecked")
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        javaSerializer.writeObject(new ArrayList((PersistentBag)obj), out);
    }

}
