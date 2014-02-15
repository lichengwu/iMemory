package cn.lichengwu.imemory.util;

import cn.lichengwu.imemory.pojo.SimpleObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2014-02-14 5:31 PM
 */
public final class PojoMockUtil {

    /**
     * mock for {@linkplain cn.lichengwu.imemory.pojo.SimpleObject}
     *
     * @return
     */
    public static SimpleObject mockObject() {
        SimpleObject object = new SimpleObject();
        object.setIntField(ThreadLocalRandom.current().nextInt());
        object.setLongField(ThreadLocalRandom.current().nextLong());
        object.setStringField("test" + ThreadLocalRandom.current().nextInt());
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(10); i++) {
            map.put(i, "map value " + i);
        }
        object.setMapField(map);
        return object;
    }

}
