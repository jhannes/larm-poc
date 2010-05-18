package no.statnett.larm.core.web.service;

import org.hibernate.collection.PersistentBag;

import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;

public class LarmHessianSerializerFactory extends SerializerFactory {

    @SuppressWarnings("unchecked")
    @Override
    protected Serializer loadSerializer(Class cl) throws HessianProtocolException {
        if (PersistentBag.class.isAssignableFrom(cl)) {
            return new HibernateBagSerializer();
        }
        return super.loadSerializer(cl);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Deserializer loadDeserializer(Class cl) throws HessianProtocolException {
        return super.loadDeserializer(cl);
    }
}
