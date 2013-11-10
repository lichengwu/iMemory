package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.Config;
import cn.lichengwu.imemory.core.constant.StorageType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * base test for {@code FixSizeMemoryBuffer}
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 12:58 PM
 */
public class FixSizeMemoryBufferTest {

    private static final Logger log = LoggerFactory.getLogger(FixSizeMemoryBufferTest.class);

    MemoryBuffer buffer;

    Config config;

    @Before
    public void setUp() {
        config = new Config();

        config.setMaximum(1024).setSliceSize(16).setStorageType(StorageType.DIRECT);

        buffer = new FixSizeMemoryBuffer();
        buffer.init(config);

    }

    @Test
    public void test() {
        Assert.assertNotNull(buffer);
        Assert.assertNotNull(config);
        Assert.assertEquals(buffer.capacity(), config.getMaximum());
        Assert.assertEquals(buffer.capacity(), buffer.maximum());

        //1. write buf
        byte[] buf = new byte[]{1, 2, 3, 4};
        int pointer = buffer.writeBytes(buf);
        Assert.assertEquals(buffer.capacity(), buffer.maximum() - config.getSliceSize());

        //2. read bytes
        byte[] read = buffer.readBytes(pointer);
        Assert.assertTrue(buf.length <= read.length);
        for (int i = 0; i < buf.length; i++) {
            Assert.assertEquals(read[i], buf[i]);
        }

        //3. clear
        buffer.clear(pointer);
        Assert.assertEquals(buffer.capacity(), buffer.maximum());

        //4. exception

        buf = new byte[config.getSliceSize() + 1];
        try {
            buffer.writeBytes(buf);
        } catch (RuntimeException e) {
            log.debug(e.getMessage(), e);
            return;
        }
        Assert.fail();
    }
}