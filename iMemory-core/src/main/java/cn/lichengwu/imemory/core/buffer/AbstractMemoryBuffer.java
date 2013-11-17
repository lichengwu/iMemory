package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.config.Config;

import java.nio.ByteBuffer;

/**
 * abstract memory buffer
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 12:04 PM
 */
public abstract class AbstractMemoryBuffer implements MemoryBuffer {

    protected volatile int capacity;

    protected volatile int maximum;

    protected volatile ByteBuffer root;

    @Override
    public void init(Config config) {
        // check config
        checkConfig(config);
        // init root buffer
        initRoot(config);
        // set size
        this.maximum = config.getMaximum();
        this.capacity = maximum();
    }

    /**
     * init inner {@linkplain ByteBuffer} according to {@linkplain Config}
     *
     * @param config
     */
    protected void initRoot(Config config) {
        switch (config.getStorageType()) {
            case HEAP:
                root = ByteBuffer.allocate(config.getMaximum());
                break;
            case DIRECT:
                root = ByteBuffer.allocateDirect(config.getMaximum());
                break;
            case DISK:
                // TODO
                throw new UnsupportedOperationException();
            case UNSAFE:
                // TODO
                throw new UnsupportedOperationException();
            default:
                throw new UnsupportedOperationException(config.getStorageType().toString());
        }
        // set byte order
        if (config.getByteOrder() != null) {
            root.order(config.getByteOrder());
        }
    }

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
