package cn.lichengwu.imemory.util;

import org.junit.Assert;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2014-03-01 12:23 PM
 */
public class MockUtil {


    /**
     * mock random byte array
     *
     * @param size array's length
     *
     * @return
     */
    public static byte[] randomBytes(int size) {
        if (size <= 0) {
            Assert.fail("byte array's length can not less tan 0");
        }
        byte[] rest = new byte[size];
        ThreadLocalRandom.current().nextBytes(rest);
        return rest;
    }
}
