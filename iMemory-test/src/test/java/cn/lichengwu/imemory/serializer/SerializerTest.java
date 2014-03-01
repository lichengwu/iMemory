package cn.lichengwu.imemory.serializer;

import cn.lichengwu.imemory.pojo.SimpleObject;
import cn.lichengwu.imemory.util.PojoMockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-14 9:42 PM
 */
public class SerializerTest {

    ArrayList<SimpleObject> objects;

    private static final int SIZE = 1000;

    @Before
    public void setUp() throws Exception {
        objects = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            objects.add(PojoMockUtil.mockObject());
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SerializerTest.class);


    @Test
    public void test() {
        try {
            serializeAndDeserialize(KryoSerializer.getInstance());
            serializeAndDeserialize(HessianSerializer.getInstance());
            serializeAndDeserialize(MessagePackSerializer.getInstance());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }
    }

    public void serializeAndDeserialize(Serializer serializer) throws IOException {
        ArrayList<byte[]> dataList = new ArrayList<>(SIZE);
        long begin = System.nanoTime();
        for (SimpleObject object : objects) {
            dataList.add(serializer.serialize(object));
        }
        log.debug(serializer.getClass().getSimpleName() + ":serialize time used:{}ns",
                (System.nanoTime() - begin) / SIZE);

        long totalSize = 0;
        for (byte[] bytes : dataList) {
            totalSize += bytes.length;
        }
        log.debug(serializer.getClass().getSimpleName() + ":serialize avg size:{}bytes", totalSize / SIZE);

        ArrayList<SimpleObject> deList = new ArrayList<>(SIZE);
        begin = System.nanoTime();
        for (byte[] bytes : dataList) {
            deList.add(serializer.deserialize(bytes, SimpleObject.class));
        }
        log.debug(serializer.getClass().getSimpleName() + ":deserialize time used:{}ns",
                (System.nanoTime() - begin) / SIZE);

        for (int i = 0; i < SIZE; i++) {
            Assert.assertEquals(objects.get(i), deList.get(i));
        }
    }

}
