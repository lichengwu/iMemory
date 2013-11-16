package cn.lichengwu.imemory.serializer;

import cn.lichengwu.imemory.pojo.SimpleObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author 佐井
 * @version 1.0
 * @created 2013-11-14 9:42 PM
 */
public class SerializerTest {


    @Test
    public void testKryo() throws IOException {
        Serializer serializer = KryoSerializer.getInstance();

        SimpleObject object = new SimpleObject();
        object.setIntField(1);
        object.setLongField(2L);
        object.setMapField(new HashMap<Integer, String>() {
            private static final long serialVersionUID = -3621090183315583328L;

            @Override
            public String put(Integer key, String value) {
                return super.put(3, "value");
            }
        });
        object.setStringField("string value");

        byte[] data = serializer.serialize(object);

        SimpleObject deserialize = serializer.deserialize(data, SimpleObject.class);

        Assert.assertEquals(deserialize, object);

    }

    @Test
    public void test() throws IOException {
        Serializer serializer = KryoSerializer.getInstance();
        System.out.println(serializer.serialize(Long.valueOf(Long.valueOf("132"))).length);
    }
}
