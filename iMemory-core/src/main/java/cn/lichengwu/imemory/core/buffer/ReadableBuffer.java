package cn.lichengwu.imemory.core.buffer;

/**
 * A buffer can read
 *
 * @author 佐井
 * @version 1.0
 * @created 2013-11-07 10:51 PM
 */
public interface ReadableBuffer {


    /**
     * read a byte
     *
     * @return
     */
    byte readByte();

    /**
     * read bytes to the param
     *
     * @param bytes array old new bytes
     *
     * @return current index
     */
    int readBytes(byte[] bytes);

    int readBytes(byte[] bytes, int offset, int length);

    boolean readBoolean();

    short readShort();

    int readInt();

    double readDouble();

    float readFloat();

    long readLong();
}
