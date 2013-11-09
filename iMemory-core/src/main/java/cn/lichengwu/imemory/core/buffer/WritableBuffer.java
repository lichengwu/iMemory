package cn.lichengwu.imemory.core.buffer;

/**
 * A buffer can write
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-07 10:52 PM
 */
public interface WritableBuffer {

    /**
     * write one byte
     *
     * @param b
     */
    void writeByte(byte b);

    /**
     * write some bytes
     *
     * @param bytes
     */
    void writeBytes(byte[] bytes);

    /**
     * write one boolean
     *
     * @param b
     */
    void writeBool(boolean b);

    /**
     * write one s
     *
     * @param s
     */
    void writeShort(short s);

    /**
     * write one int
     *
     * @param i
     */
    void writeInt(int i);

    /**
     * write one double
     *
     * @param d
     */
    void writeDouble(double d);

    /**
     * write one float
     *
     * @param f
     */
    void writeFloat(float f);

    /**
     * write a char
     *
     * @param c
     */
    void writeChar(char c);

    /**
     * write chars
     *
     * @param chars
     */
    void writeChars(char[] chars);

    /**
     * write one long
     *
     * @param l
     */
    void writeLong(long l);

}
