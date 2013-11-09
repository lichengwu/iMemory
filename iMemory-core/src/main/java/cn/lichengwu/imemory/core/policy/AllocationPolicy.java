package cn.lichengwu.imemory.core.policy;

import cn.lichengwu.imemory.core.allocator.Allocator;

import java.util.Collection;

/**
 * buffer allocation policy
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-09 5:23 PM
 */
public interface AllocationPolicy {

    /**
     * initialize the policy by a {@linkplain Allocator} list.
     *
     * @param allocators for init
     */
    void init(Collection<Allocator> allocators);

    /**
     * get next fee {@link Allocator} to use to allocate.
     *
     * @param previous the previously used {@link Allocator}, or null if it's the first allocation
     * @param tryTimes try the number of time the allocation has already failed.
     *
     * @return the {@link Allocator} to use, or null if allocation has failed.
     */
    Allocator getAvailableAllocator(Allocator previous, int tryTimes);

    /**
     * reset internal state
     */
    void reset();
}
