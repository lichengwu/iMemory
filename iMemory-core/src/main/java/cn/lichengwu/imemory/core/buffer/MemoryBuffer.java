package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.config.Config;

import java.nio.ByteOrder;

/**
 * A buffer base on memory
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-07 10:50 PM
 */
public interface MemoryBuffer {


    /**
     * init the ByteBuffer use the config
     *
     * @param config
     */
    void init(Config config);

    /**
     * @return current memory size in byte
     */
    long capacity();

    /**
     * @return max size of the memory byte
     */
    int maximum();

    /**
     * clear memory and reset inner status
     */
    void clear();

    /**
     * tell the memory's current status
     *
     * @return return true while the memory is changing, otherwise false.
     */
    //    boolean changing();

    /**
     * @return byte order in current memory
     * @see ByteOrder#BIG_ENDIAN
     * @see ByteOrder#LITTLE_ENDIAN
     */
    ByteOrder getOrder();


    /**
     * write bytes to memory
     *
     * @param bytes
     *
     * @return the data offset in storage
     */
    int writeBytes(byte[] bytes);

    /**
     * read byte under index
     *
     * @param index the data offset in storage
     *
     * @return the value under the index or null if the index isn't be used.
     */
    byte[] readBytes(int index);

    /**
     * clear data from storage associate whit index
     *
     * @param index
     */
    void clear(int index);
}
