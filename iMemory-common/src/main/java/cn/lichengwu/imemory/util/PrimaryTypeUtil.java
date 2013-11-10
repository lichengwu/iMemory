package cn.lichengwu.imemory.util;

/**
 * A util for java primary type
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 3:43 PM
 */
public final class PrimaryTypeUtil {
    private PrimaryTypeUtil() {
    }

    /**
     * get a short from int
     *
     * @param value origin int
     * @param high  false:low-endian,true:high-endian
     *
     * @return int's high or low endian value in short
     */
    public static short getIntEndian(int value, boolean high) {
        if (high)
            return (short) ((value & 0xFFFF0000) >>> 16);

        return (short) (value & 0x0000FFFF);
    }

    /**
     * set a int's high or low endian from a short
     *
     * @param origin origin int value
     * @param value  shot value to be set
     * @param high   false:low-endian,true:high-endian
     *
     * @return new int value witch low-endian or high-endian was set to the short value
     */
    public static int setIntEndian(int origin, short value, boolean high) {
        int cast = value;
        if (cast < 0)
            cast = cast + (1 << 16);
        if (high) {
            return (origin & 0x0000FFFF) | (cast << 16);
        } else
            return (origin & 0xFFFF0000) | cast;
    }

    /**
     * set a long's high or low endian from a int
     *
     * @param origin origin long value
     * @param value  int value to be set
     * @param high   false:low-endian,true:high-endian
     *
     * @return new long value witch low-endian or high-endian was set to the int value
     */
    public static long setLongEndian(long origin, int value, boolean high) {
        long cast = value;
        if (cast < 0)
            cast = cast + (1L << 32);
        if (high) {
            return (origin & 0x00000000FFFFFFFFL) | (cast << 32);
        } else
            return (origin & 0xFFFFFFFF00000000L) | cast;
    }

    /**
     * get a int from long
     *
     * @param value origin long
     * @param high  false:low-endian,true:high-endian
     *
     * @return long's high or low endian value in int
     */
    public static int getLongEndian(long value, boolean high) {
        if (high)
            return (int) ((value & 0xFFFFFFFF00000000L) >>> 32);

        return (int) (value & 0x00000000FFFFFFFFL);
    }

    /**
     * compress tow int to one long
     *
     * @param high high-endian
     * @param low  low-endian
     *
     * @return new long
     */

    public static long combineToLong(int high, int low) {
        long value = 0L;
        value = setLongEndian(value, high, true);
        value = setLongEndian(value, low, false);
        return value;
    }
}
