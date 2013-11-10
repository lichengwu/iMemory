package cn.lichengwu.imemory.core.config;

import cn.lichengwu.imemory.core.constant.StorageType;

import java.nio.ByteOrder;

/**
 * Config for iMemory
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 11:43 AM
 */
public class Config {

    private static final int DEFAULT_CONCURRENT_LEVEL = 8;

    /**
     * @see ByteOrder
     */
    private ByteOrder byteOrder;

    /**
     * max size of the storage
     */
    private int maximum;

    /**
     * @see cn.lichengwu.imemory.core.buffer.FixSizeMemoryBuffer
     */
    private int sliceSize;

    /**
     * inner storage type
     */
    private StorageType storageType;

    /**
     * concurrent level for multi-thread access
     */
    private int concurrentLevel = DEFAULT_CONCURRENT_LEVEL;


    public StorageType getStorageType() {
        return storageType;
    }

    public int getSliceSize() {
        return sliceSize;
    }

    public int getMaximum() {
        return maximum;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public Config setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        return this;
    }

    public Config setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }

    public Config setSliceSize(int sliceSize) {
        this.sliceSize = sliceSize;
        return this;
    }

    public Config setStorageType(StorageType storageType) {
        this.storageType = storageType;
        return this;
    }

    public int getConcurrentLevel() {
        return concurrentLevel;
    }

    public Config setConcurrentLevel(int concurrentLevel) {
        this.concurrentLevel = concurrentLevel;
        return this;
    }
}
