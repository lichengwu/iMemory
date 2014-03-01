package cn.lichengwu.imemory.core.manager;

import cn.lichengwu.imemory.annotation.ThreadSafe;
import cn.lichengwu.imemory.core.buffer.FixSizeMemoryBuffer;
import cn.lichengwu.imemory.core.buffer.MemoryBuffer;
import cn.lichengwu.imemory.core.buffer.MergingMemoryBuffer;
import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.util.PrimaryTypeUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread safe MemoryManager for {@linkplain MemoryBuffer} to CRUD the data in memory
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 3:01 PM
 */
@ThreadSafe
public class MemoryManager {

    private static final int MAX_CONCURRENT_LEVEL = 64;

    private final AtomicInteger segmentIndexCounter = new AtomicInteger(0);

    private MemoryBuffer[] memoryBufferSegments;

    private Lock[] segmentLocks;

    private int concurrentLevel;

    public MemoryManager(Config config) {
        if (config.getConcurrentLevel() < 1 || config.getConcurrentLevel() > MAX_CONCURRENT_LEVEL) {
            throw new IllegalArgumentException("concurrent level must between 1 and " + MAX_CONCURRENT_LEVEL);
        }
        this.concurrentLevel = config.getConcurrentLevel();
        this.memoryBufferSegments = new MemoryBuffer[concurrentLevel];
        this.segmentLocks = new ReentrantLock[concurrentLevel];
        //        long maximum = config.getMaximum() / concurrentLevel + 1;
        //        if (maximum > Integer.MAX_VALUE) {
        //            throw new IllegalArgumentException("can not create buffer, config's maximum is too larger. " +
        //                    "Try to decrease the maximum or enlarge concurrent level in config.");
        //        }
        //        config.setMaximum(maximum);
        for (int i = 0; i < concurrentLevel; i++) {
            //new lock
            segmentLocks[i] = new ReentrantLock();
            // new MemoryBuffer
            MemoryBuffer buffer;
            switch (config.getStoragePolicy()) {
                case FIX_SIZE:
                    buffer = new FixSizeMemoryBuffer();
                    break;
                case MERGE:
                    buffer = new MergingMemoryBuffer();
                    break;
                default:
                    buffer = null;
            }
            buffer.init(config);
            memoryBufferSegments[i] = buffer;
        }
    }

    /**
     * put a byte array to memory
     *
     * @param bytes
     *
     * @return a long which compress segment index(int) and pointer(int) together
     */
    public long insert(byte[] bytes) {
        //get segment index
        int segmentIndex = getNextSegmentIndex();
        Lock lock = segmentLocks[segmentIndex];
        lock.lock();
        try {
            int pointer = memoryBufferSegments[segmentIndex].writeBytes(bytes);
            //compress segment index and pointer to one long
            return PrimaryTypeUtil.combineToLong(segmentIndex, pointer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * remove data from memory with a key
     *
     * @param key a long which compress segment index(int) and pointer(int) together
     */
    public void delete(long key) {
        int segmentIndex = PrimaryTypeUtil.getLongEndian(key, true);
        segmentLocks[segmentIndex].tryLock();
        try {
            int pointer = PrimaryTypeUtil.getLongEndian(key, false);
            memoryBufferSegments[segmentIndex].clear(pointer);
        } finally {
            segmentLocks[segmentIndex].unlock();
        }
    }

    /**
     * read data with the given key and then remove the k-v
     *
     * @param key
     *
     * @return the data associate whit the given key
     */
    public byte[] readAndDelete(long key) {
        int segmentIndex = PrimaryTypeUtil.getLongEndian(key, true);
        segmentLocks[segmentIndex].tryLock();
        try {
            int pointer = PrimaryTypeUtil.getLongEndian(key, false);
            MemoryBuffer segment = memoryBufferSegments[segmentIndex];
            byte[] data = segment.readBytes(pointer);
            segment.clear(pointer);
            return data;
        } finally {
            segmentLocks[segmentIndex].unlock();
        }
    }

    /**
     * get data from memory by key
     *
     * @param key a long which compress segment index(int) and pointer(int) together
     *
     * @return
     */
    public byte[] get(long key) {
        int segmentIndex = PrimaryTypeUtil.getLongEndian(key, true);
        int pointer = PrimaryTypeUtil.getLongEndian(key, false);
        return memoryBufferSegments[segmentIndex].readBytes(pointer);
    }

    //    /**
    //     * update key's value in memory
    //     *
    //     * @param key
    //     * @param newValue
    //     *
    //     * @return old byte array
    //     */
    //    public byte[] update(long key, byte[] newValue) {
    //        int segmentIndex = PrimaryTypeUtil.getLongEndian(key, true);
    //        segmentLocks[segmentIndex].tryLock();
    //        try {
    //            MemoryBuffer currentSegment = memoryBufferSegments[segmentIndex];
    //            int pointer = PrimaryTypeUtil.getLongEndian(key, false);
    //            byte[] oldValue = currentSegment.readBytes(pointer);
    //            currentSegment.clear(pointer);
    //            currentSegment.writeBytes(newValue);
    //            return oldValue;
    //        } finally {
    //            segmentLocks[segmentIndex].unlock();
    //        }
    //    }

    //    /**
    //     * update without read old value.
    //     * </p>
    //     * faster than {@linkplain #update(long, byte[])}
    //     *
    //     * @param key
    //     * @param newValue
    //     */
    //    public void fastUpdate(long key, byte[] newValue) {
    //        int segmentIndex = PrimaryTypeUtil.getLongEndian(key, true);
    //        segmentLocks[segmentIndex].tryLock();
    //        try {
    //            MemoryBuffer currentSegment = memoryBufferSegments[segmentIndex];
    //            int pointer = PrimaryTypeUtil.getLongEndian(key, false);
    //            currentSegment.clear(pointer);
    //            currentSegment.writeBytes(newValue);
    //        } finally {
    //            segmentLocks[segmentIndex].unlock();
    //        }
    //    }

    /**
     * @return the capacity of the manager
     */
    public long capacity() {
        long cap = 0L;
        for (MemoryBuffer memoryBuffer : memoryBufferSegments) {
            cap += memoryBuffer.capacity();
        }
        return cap;
    }

    /**
     * clear all data in this manager  and reset status
     */
    public void clear() {
        for (MemoryBuffer memoryBuffer : memoryBufferSegments) {
            memoryBuffer.clear();
        }
        this.segmentIndexCounter.set(0);
    }


    /**
     * this method using a counter increase to ensue every MemoryBuffer in this manager
     * </p>
     * has the same equality to be written
     *
     * @return
     */
    private int getNextSegmentIndex() {
        while (true) {
            int current = segmentIndexCounter.get();
            int nextSegmentIndex = current + 1;
            if (current < 0) {
                segmentIndexCounter.set(0);
                continue;
            }
            if (segmentIndexCounter.compareAndSet(current, nextSegmentIndex)) {
                return nextSegmentIndex % concurrentLevel;
            }
        }
    }
}
