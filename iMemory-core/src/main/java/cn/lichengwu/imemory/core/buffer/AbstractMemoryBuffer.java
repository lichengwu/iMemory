package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.Config;

/**
 * abstract memory buffer
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 12:04 PM
 */
public abstract class AbstractMemoryBuffer implements MemoryBuffer {

    protected int capacity;

    protected int maximum;

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public int maximum() {
        return maximum;
    }

    protected void checkConfig(Config config) {
        if (config == null) {
            throw new NullPointerException("config can not be null");
        }
        if (config.getStorageType() == null) {
            throw new IllegalArgumentException("please set StorageType in the Config");
        }
        if (config.getMaximum() <= 0) {
            throw new IllegalArgumentException("capacity muse greater than 0");
        }
    }

}
