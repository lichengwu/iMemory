package cn.lichengwu.imemory.serializer;

import java.io.IOException;

/**
 * TODO 方法说明
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-06 10:48 PM
 */

public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] source, Class<T> clazz);

}
