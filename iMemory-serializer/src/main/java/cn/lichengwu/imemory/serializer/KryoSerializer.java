package cn.lichengwu.imemory.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A serializer using {@linkplain Kryo} lib.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-12 10:35 PM
 */
public class KryoSerializer implements Serializer {

    private static final int BUFFER_SIZE = 4096;

    private KryoPool pool;

    private KryoSerializer() {
        this.pool = new KryoPool();
    }

    public static KryoSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        Class<?> clazz = obj.getClass();

        KryoHolder kryoHolder = null;
        try {
            kryoHolder = pool.get();
            kryoHolder.kryo.reset();
            kryoHolder.kryo.register(clazz);

            kryoHolder.kryo.writeObject(kryoHolder.output, obj);
            return kryoHolder.output.toBytes();
        } finally {
            if (kryoHolder != null) {
                pool.done(kryoHolder);
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] source, Class<T> clazz) {
        KryoHolder kryoHolder = null;
        try {
            kryoHolder = pool.get();
            kryoHolder.kryo.register(clazz);

            Input input = new Input(source);
            return kryoHolder.kryo.readObject(input, clazz);
        } finally {
            if (kryoHolder != null) {
                pool.done(kryoHolder);
            }
        }
    }

    /**
     * clear cached Kryo
     *
     * @throws IOException
     */
    public void close() throws IOException {
        pool.close();
    }

    /**
     * a thread safe Kryo pool.
     */
    private static class KryoPool {
        Queue<KryoHolder> pool = new ConcurrentLinkedQueue<KryoHolder>();


        public KryoHolder get() {
            KryoHolder kryoHolder = pool.poll();
            if (kryoHolder == null) {
                Kryo kryo = new Kryo();
                kryo.setReferences(false);
                kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
                kryoHolder = new KryoHolder(kryo);
            }
            return kryoHolder;
        }

        public void done(KryoHolder kryoHolder) {
            pool.offer(kryoHolder);
        }

        public void close() {
            pool.clear();
        }
    }

    /**
     * holder for {@linkplain Kryo} and {@linkplain Output}.
     * </p>
     * Kryo is not thread safe.
     * Each thread should have its own Kryo, Input, and Output instances.
     */
    private static class KryoHolder {
        Kryo kryo;
        Output output;

        KryoHolder(Kryo kryo) {
            this.kryo = kryo;
            this.output = new Output(BUFFER_SIZE, -1);
        }
    }

    private static class InstanceHolder {
        static final KryoSerializer INSTANCE = new KryoSerializer();
    }
}
