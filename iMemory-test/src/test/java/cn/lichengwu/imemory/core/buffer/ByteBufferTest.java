package cn.lichengwu.imemory.core.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 9:39 AM
 */
public class ByteBufferTest {

    @Test
    public void test() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer put = buffer.put(new byte[]{1, 1, 1, 1});
        ByteBuffer put1 = buffer.put(new byte[]{1, 1, 1, 1});
        System.out.println(put.limit());
        System.out.println(put.capacity());
        System.out.println(put.arrayOffset());
        System.out.println(put.remaining());
    }
}
