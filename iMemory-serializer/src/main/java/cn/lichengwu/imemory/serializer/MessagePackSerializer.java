package cn.lichengwu.imemory.serializer;

import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

import java.io.IOException;

/**
 * A serializer using {@linkplain MessagePack} lib.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-14 10:32 PM
 */
public class MessagePackSerializer implements Serializer {

    MessagePack messagePack;

    public static MessagePackSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private MessagePackSerializer() {
        messagePack = new MessagePack();
    }

    public <T> byte[] serialize(T obj) throws IOException {
        registerClass(obj.getClass());
        return messagePack.write(obj);
    }

    public <T> T deserialize(byte[] source, Class<T> clazz) throws IOException {
        registerClass(clazz);
        return messagePack.read(source, clazz);
    }

    public void close() throws IOException {
        messagePack.unregister();
        messagePack = null;
    }

    private void registerClass(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Message.class)) {
            return;
        }
        if (messagePack.lookup(clazz) != null) {
            return;
        }
        messagePack.register(clazz);
    }

    private static class InstanceHolder {
        static final MessagePackSerializer INSTANCE = new MessagePackSerializer();
    }
}
