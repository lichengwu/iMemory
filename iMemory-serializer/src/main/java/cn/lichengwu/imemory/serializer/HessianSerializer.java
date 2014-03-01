package cn.lichengwu.imemory.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A serializer using {@linkplain Hessian} lib.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-16 2:48 PM
 */
public class HessianSerializer implements Serializer {

    private SerializerFactory serializerFactory = SerializerFactory.createDefault();

    private HessianSerializer() {
    }

    public static HessianSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(baos);
        ho.setSerializerFactory(serializerFactory);
        ho.writeObject(obj);
        return baos.toByteArray();
    }

    public <T> T deserialize(byte[] source, Class<T> clazz) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(source);
        HessianInput hi = new HessianInput(bais);
        hi.setSerializerFactory(serializerFactory);
            return (T) hi.readObject(clazz);
    }

    public void close() throws IOException {
        // do nothing
    }

    private static class InstanceHolder {
        static final HessianSerializer INSTANCE = new HessianSerializer();
    }
}
