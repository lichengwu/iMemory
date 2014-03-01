package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.core.constant.StoragePolicy;
import cn.lichengwu.imemory.core.constant.StorageType;
import cn.lichengwu.imemory.util.MockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * base test for {@code FixSizeMemoryBuffer}
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 12:58 PM
 */
public class MemoryBufferTest {

    private static final Logger log = LoggerFactory.getLogger(MemoryBufferTest.class);

    private MemoryBuffer buffer;

    private Config config;

    @Before
    public void setUp() {

    }

    /**
     * config and make a FixSizeMemoryBuffer
     */
    private void makeFixSizeMemoryBuffer() {
        try {
            if (buffer != null) {
                buffer.clear();
            }
            config = new Config();
            config.setMaximum(1024).setSliceSize(16).setStorageType(StorageType.DIRECT)
                    .setByteOrder(ByteOrder.LITTLE_ENDIAN).setStoragePolicy(StoragePolicy.FIX_SIZE);
            buffer = new FixSizeMemoryBuffer();
            buffer.init(config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * config and make a MergingMemoryBuffer
     */
    private void makeMergingMemoryBuffer() {
        try {
            if (buffer != null) {
                buffer.clear();
            }
            config = new Config();
            config.setMaximum(1024).setSliceSize(16).setStorageType(StorageType.DIRECT)
                    .setByteOrder(ByteOrder.LITTLE_ENDIAN).setStoragePolicy(StoragePolicy.FIX_SIZE);
            buffer = new MergingMemoryBuffer();
            buffer.init(config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void test() {
        try {
            // test for MergingMemoryBuffer
            testByClazz(MergingMemoryBuffer.class);
            // test for  FixSizeMemoryBuffer
            testByClazz(FixSizeMemoryBuffer.class);

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    public void testByClazz(Class<? extends MemoryBuffer> clazz) {

        if (clazz.equals(FixSizeMemoryBuffer.class)) {
            makeFixSizeMemoryBuffer();
        } else if (clazz.equals(MergingMemoryBuffer.class)) {
            makeMergingMemoryBuffer();
        } else {
            Assert.fail("not match class");
        }

        //0. check config and buffer
        Assert.assertNotNull(buffer);
        Assert.assertNotNull(config);
        Assert.assertEquals(buffer.capacity(), config.getMaximum());
        Assert.assertEquals(buffer.capacity(), buffer.maximum());

        //1. write buf
        byte[] buf = MockUtil.randomBytes(10);
        int pointer1 = buffer.writeBytes(buf);
        Assert.assertTrue(buffer.capacity() <= buffer.maximum() - buf.length);

        int pointer2 = buffer.writeBytes(buf);
        Assert.assertTrue(buffer.capacity() <= buffer.maximum() - 2 * buf.length);

        //2. read bytes
        byte[] read1 = buffer.readBytes(pointer1);
        byte[] read2 = buffer.readBytes(pointer2);
        Assert.assertTrue(Arrays.equals(read1, read2));
        Assert.assertTrue(buf.length <= read1.length);
        for (int i = 0; i < buf.length; i++) {
            Assert.assertEquals(read1[i], buf[i]);
        }

        //3. clear
        buffer.clear(pointer1);
        buffer.clear(pointer2);
        Assert.assertEquals(buffer.capacity(), buffer.maximum());

    }
}