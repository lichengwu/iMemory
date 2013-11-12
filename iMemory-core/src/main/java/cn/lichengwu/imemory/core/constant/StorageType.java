package cn.lichengwu.imemory.core.constant;

/**
 * the inner store type in {@linkplain cn.lichengwu.imemory.core.buffer.MemoryBuffer}
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-10 11:48 AM
 */
public enum StorageType implements ConstantDefinition {

    DIRECT(1, "direct memory"),
    HEAP(2, "heap memory"),
    DISK(3, "disk");

    private StorageType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;

    private String name;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
