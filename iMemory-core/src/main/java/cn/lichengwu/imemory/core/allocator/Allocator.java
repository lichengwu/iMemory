package cn.lichengwu.imemory.core.allocator;

import cn.lichengwu.imemory.core.buffer.MemoryBuffer;

/**
 * allocate/free {@linkplain MemoryBuffer}.
 * </p>
 * {@linkplain Allocator} is something like a byte pool.
 * Use it's {@linkplain Allocator#free(cn.lichengwu.imemory.core.buffer.MemoryBuffer)} and {@linkplain Allocator#allocate(int)}
 * to access and return inner data.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-09 4:43 PM
 */
public interface Allocator {

    /**
     * allocate a {@linkplain MemoryBuffer} with the given size.
     * <p/>
     * if there is not enough free space in {@linkplain Allocator}, a {@linkplain java.nio.BufferOverflowException}
     * will be threw.
     *
     * @param size size in byte to be allocated.
     *
     * @return a {@linkplain MemoryBuffer} with the given size.
     */
    MemoryBuffer allocate(int size);

    /**
     * free a {@linkplain MemoryBuffer} from the {@linkplain Allocator}. Making it available for next usage.
     * <p/>
     * Call more than one times this method will be ok.
     *
     * @param memoryBuffer the {@linkplain MemoryBuffer} to be released.
     */
    void free(MemoryBuffer memoryBuffer);

    /**
     * clean all {@linkplain MemoryBuffer}s in the Allocator, so the Allocator is empty for future usage.
     */
    void clear();

    /**
     * @return the total capacity the can be allocated.
     */
    long capacity();
}
