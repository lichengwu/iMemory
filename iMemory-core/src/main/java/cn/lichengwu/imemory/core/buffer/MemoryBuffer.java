package cn.lichengwu.imemory.core.buffer;

import java.nio.ByteOrder;

/**
 * A buffer base on memory
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-07 10:50 PM
 */
public interface MemoryBuffer extends ReadableBuffer, WritableBuffer {

    /**
     * @return current memory size in byte
     */
    long capacity();

    /**
     * @return max size of the memory byte
     */
    long maximum();

    /**
     * free memory usage
     */
    void free();

    /**
     * clear memory and reset inner status
     */
    void clear();

    /**
     * tell the memory's current status
     *
     * @return return true while the memory is changing, otherwise false.
     */
    boolean changing();

    /**
     * @return byte order in current memory
     */
    ByteOrder getOrder();

    /**
     * change the memory's byte order
     *
     * @param byteOrder new byte order
     *
     * @see ByteOrder#BIG_ENDIAN
     * @see ByteOrder#LITTLE_ENDIAN
     */
    void setOrder(ByteOrder byteOrder);
}
