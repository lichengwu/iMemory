package cn.lichengwu.imemory.core.constant;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2014-02-17 9:12 PM
 */
public enum StoragePolicy implements ConstantDefinition {

    FIX_SIZE(1, "FIX SIZE"),
    MERGE(2, "MERGE");

    private StoragePolicy(int code, String name) {
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
