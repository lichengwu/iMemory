package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.util.FastIntegerStack;

import java.nio.BufferOverflowException;
import java.nio.ByteOrder;

/**
 * Split the space into {@code sliceSize}, it means than every element store in this MemoryBuffer can not bigger than
 * {@code sliceSize}.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-09 3:16 PM
 */
public class FixSizeMemoryBuffer extends AbstractMemoryBuffer {

    private static final byte EMPTY_BYTE = 0;

    // a free pointer stack
    private volatile FastIntegerStack freePointers;

    // mark whether the pointer is use.
    // the array's index is pointer and element under index is the flag.
    private volatile boolean[] flags;

    // the size of every piece
    private volatile int sliceSize;

    @Override
    public void init(Config config) {

        super.init(config);

        if (config.getSliceSize() <= 0) {
            throw new IllegalArgumentException("slice size must greater then 0");
        }
        this.sliceSize = config.getSliceSize();

        this.flags = new boolean[this.maximum / sliceSize + 1];

        //init free pointers
        freePointers = new FastIntegerStack(this.maximum / sliceSize);
        for (int i = this.maximum / sliceSize - 1; i >= 0; i--) {
            freePointers.push(i);
        }

    }

    @Override
    public void clear() {
        root.clear();
        capacity = maximum();
    }

    @Override
    public ByteOrder getOrder() {
        return root.order();
    }

    @Override
    public int writeBytes(byte[] bytes) {

        // usage check
        if (freePointers.empty()) {
            throw new BufferOverflowException();
        }
        if (bytes.length > sliceSize) {
            throw new IllegalArgumentException("bytes can not larger than " + sliceSize);
        }
        // pop free pointer
        int index = freePointers.pop();

        //write bytes
        int offset = index * sliceSize;
        root.position(offset);
        root.put(bytes);

        // set used
        flags[index] = true;
        // set capacity
        this.capacity -= sliceSize;
        return index;
    }

    @Override
    public byte[] readBytes(int index) {
        if (flags[index]) {
            // set position
            root.position(index * sliceSize);
            byte[] buffs = new byte[sliceSize];
            // read
            root.get(buffs);
            return buffs;
        }
        return null;
    }

    @Override
    public void clear(int index) {

        // give back pointer to the stack
        freePointers.push(index);
        root.position(index);

        //erase data
        for (int i = 0; i < sliceSize; i++) {
            root.put(EMPTY_BYTE);
        }
        // set used
        flags[index] = false;
        //set capacity
        capacity += sliceSize;
    }
}
