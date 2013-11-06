package cn.lichengwu.imemory.serializer;

import cn.lichengwu.imemory.annotation.ThreadSafe;

/**
 * Declare serialize and deserialize method.
 * All implements must thread safe.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-06 10:48 PM
 */

@ThreadSafe
public interface Serializer {

    /**
     * serialize java object to byte array.
     *
     * @param obj
     * @param <T>
     *
     * @return byte array
     */
    <T> byte[] serialize(T obj);

    /**
     * unfreeze java object from byte array
     *
     * @param source source byte array hold java object info
     * @param clazz  target class to be unfrozen
     * @param <T>
     *
     * @return
     */
    <T> T deserialize(byte[] source, Class<T> clazz);

}
