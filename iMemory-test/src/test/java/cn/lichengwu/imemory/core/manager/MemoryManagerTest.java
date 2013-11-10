package cn.lichengwu.imemory.core.manager;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.core.constant.StorageType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 4:44 PM
 */
public class MemoryManagerTest {

    private MemoryManager memoryManager;
    private Config config;

    @Before
    public void setUp() {
        config = new Config();
        config.setMaximum(1024 * 1024).
                setStorageType(StorageType.DIRECT).setSliceSize(100).setConcurrentLevel(4)
                .setByteOrder(ByteOrder.LITTLE_ENDIAN);
        memoryManager = new MemoryManager(config);
    }

    @Test
    public void test() {
        Assert.assertNotNull(config);
        Assert.assertNotNull(memoryManager);
        Assert.assertTrue(memoryManager.capacity() >= config.getMaximum());
        long oldCapacity = memoryManager.capacity();
        byte[] buf = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        //1. insert
        long key = memoryManager.insert(buf);
        Assert.assertTrue(oldCapacity > memoryManager.capacity());
        byte[] newBuf = new byte[]{1, 2, 3, 4, 5};
        //2. update
        byte[] oldBuf = memoryManager.update(key, newBuf);
        for (int i = 0; i < buf.length; i++) {
            Assert.assertEquals(buf[i], oldBuf[i]);
        }
        //3. get
        oldBuf = memoryManager.get(key);
        for (int i = 0; i < newBuf.length; i++) {
            Assert.assertEquals(newBuf[i], oldBuf[i]);
        }
        //4. delete
        memoryManager.delete(key);
        Assert.assertNull(memoryManager.get(key));
    }

}
