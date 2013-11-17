package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.config.Config;

import java.nio.ByteOrder;
import java.util.*;

/**
 * A {@linkplain MemoryBuffer} implementation with buffer merging capacity
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-17 7:43 PM
 */
public class MergingMemroyBuffer extends AbstractMemoryBuffer {

    private static final double DEFAULT_SIZE_RATIO_THRESHOLD = 0.9;

    private static final int DEFAULT_MIN_SIZE_THRESHOLD = 64;

    /**
     * use for init free size rang.
     */
    private static final int SIZE_LEVEL = 4;


    // key->the data's position in root ByteBuffer
    // value->the data's length and it's previous and next sibling
    private volatile Map<Integer, Link> usedMap = new HashMap<Integer, Link>();

    // key->size
    // value->free buffer's Link which capacity is the size
    private volatile NavigableMap<Integer, Collection<Link>> freeMap = new TreeMap<Integer, Collection<Link>>();

    // Min size of the returned buffer before splitting
    private int minSizeThreshold = DEFAULT_MIN_SIZE_THRESHOLD;

    // Allowed size ratio (requested size / buffer's size) of the returned buffer before splitting
    private double sizeRatioThreshold = DEFAULT_SIZE_RATIO_THRESHOLD;


    @Override
    public void init(Config config) {
        super.init(config);
        // init free map
        for (int size : generateFreeRange(config.getMaximum())) {
            freeMap.put(size, new LinkedHashSet<Link>());
        }
        // init first buffer
        initFirstBuffer();
    }

    @Override
    public void clear() {
        root.clear();
        this.capacity = maximum();
        initFirstBuffer();
    }

    @Override
    public ByteOrder getOrder() {
        return root.order();
    }

    @Override
    public int writeBytes(byte[] bytes) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] readBytes(int index) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear(int index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Generate free size list, for init only.
     * </p>
     * This is init freeMap, after some allocation or remove, the free size will split or merge.
     *
     * @param capacity
     *
     * @return a list of all size's level used by the allocator.
     */
    private List<Integer> generateFreeRange(int capacity) {
        List<Integer> range = new ArrayList<Integer>();

        for (int i = minSizeThreshold; i < capacity; i *= SIZE_LEVEL) {
            range.add(i);
        }
        if (range.isEmpty() || !range.contains(capacity)) {
            range.add(capacity);
        }
        return range;
    }

    /**
     * init first buffer.
     * <p/>
     * this method is only used for init.
     */
    private void initFirstBuffer() {
        root.clear();
        getFreeLinkCollection(new Link(0, this.minSizeThreshold, null, null));
    }

    private Collection<Link> getFreeLinkCollection(final Link link) {
        final int size = link.length - 1;
        return freeMap.ceilingEntry(size).getValue();
    }

    /**
     * a link to data
     */
    private static class Link {
        // data's offset in root buffer
        int position;
        // data's length
        int length;
        Link previous;
        Link next;

        private Link(int position, int length, Link previous, Link next) {
            this.position = position;
            this.length = length;
            this.previous = previous;
            this.next = next;
        }
    }
}
