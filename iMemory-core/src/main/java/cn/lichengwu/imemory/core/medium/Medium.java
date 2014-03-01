package cn.lichengwu.imemory.core.medium;

import java.nio.ByteOrder;

/**
 * storage medium interface.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2014-03-01 11:56 PM
 * @see cn.lichengwu.imemory.core.constant.StorageType
 */
public interface Medium {


    /**
     * put bytes to medium
     *
     * @param offset offset of the medium
     * @param bytes  data
     *
     * @return current {@linkplain cn.lichengwu.imemory.core.medium.Medium}
     */
    Medium put(int offset, byte[] bytes);

    /**
     * get data from the medium and fill them in bytes.
     *
     * @param offset offset of the medium
     * @param bytes  container for hold bytes
     */
    void get(int offset, byte[] bytes);

    /**
     * get one byte at the offset
     *
     * @param offset offset of the medium
     *
     * @return a byte at offset
     */
    byte get(int offset);

    /**
     * @return this medium's position
     */
    int position();

    /**
     * set this medium's position
     *
     * @param newPosition
     *
     * @return current {@linkplain cn.lichengwu.imemory.core.medium.Medium}
     */
    Medium position(int newPosition);

    /**
     * @return byte order in current media
     * @see ByteOrder#BIG_ENDIAN
     * @see ByteOrder#LITTLE_ENDIAN
     */
    ByteOrder order();

    /**
     * set byte order in current media
     *
     * @param byteOrder
     *
     * @return current {@linkplain cn.lichengwu.imemory.core.medium.Medium}
     * @see ByteOrder#BIG_ENDIAN
     * @see ByteOrder#LITTLE_ENDIAN
     */
    Medium order(ByteOrder byteOrder);

    /**
     * clear current medium
     *
     * @return current {@linkplain cn.lichengwu.imemory.core.medium.Medium}
     */
    Medium clear();

}
