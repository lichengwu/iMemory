package cn.lichengwu.imemory.core.buffer;

import cn.lichengwu.imemory.core.config.Config;

import java.nio.BufferOverflowException;
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

    private static final int DEFAULT_MIN_SIZE_THRESHOLD = 64;

    private static final int EMPTY_LENGTH = -1;

    /**
     * use for init free size rang.
     */
    private static final int SIZE_LEVEL = 4;


    // key->the data's position in root ByteBuffer
    // value->the data's length and it's previous and next sibling
    private volatile Map<Integer, Link> usedMap = new HashMap<>();

    // key->size
    // value->free buffer's Link which capacity is the size
    private volatile NavigableMap<Integer, LinkedList<Link>> freeMap = new TreeMap<>();

    // Min size of the returned buffer before splitting
    private int minSizeThreshold = DEFAULT_MIN_SIZE_THRESHOLD;

    // Allowed size ratio (requested size / buffer's size) of the returned buffer before splitting
    private static final double DEFAULT_SIZE_RATIO_THRESHOLD = 0.9;


    @Override
    public void init(Config config) {
        super.init(config);
        // init free map
        for (int size : generateFreeRange(config.getMaximum())) {
            freeMap.put(size, new LinkedList<Link>());
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
        int requiredSize = bytes.length;
        // get links which length greater than or equal bytes.length
        SortedMap<Integer, LinkedList<Link>> tailMap = freeMap.tailMap(requiredSize);
        // find from free space
        for (Collection<Link> links : tailMap.values()) {
            for (Link link : links) {
                if (link.length >= requiredSize) {
                    splitIfNeeded(link, requiredSize);
                    root.put(bytes, link.position, requiredSize);
                    usedMap.put(link.position, link);
                    return link.position;
                }
            }
        }
        //can not find suitable free space in freeMap
        int position = root.position();
        if (root.remaining() < requiredSize) {
            throw new BufferOverflowException();
        }
        //put into new position
        root.put(bytes);
        //new link
        Link lastLink = freeMap.lastEntry().getValue().getLast();
        Link link = new Link(position, requiredSize, lastLink, null);
        lastLink.next = link;
        usedMap.put(position, link);
        return position;
    }

    /**
     * split link into smaller slices, if waste space greater than sizeRatioThreshold
     *
     * @param link
     * @param requiredSize
     */
    private void splitIfNeeded(Link link, int requiredSize) {
        if (link.length > minSizeThreshold && (requiredSize / link.length) < DEFAULT_SIZE_RATIO_THRESHOLD) {
            Link split = new Link(link.position + requiredSize, EMPTY_LENGTH, link, link.next);
            link.next = split;
            giveBackFreeLink(split);
        }
    }

    @Override
    public byte[] readBytes(int index) {
        Link link = usedMap.get(index);
        if (link == null || link.length == EMPTY_LENGTH) {
            return null;
        }
        byte[] bytes = new byte[link.length];
        root.get(bytes, link.position, link.length);
        return bytes;
    }

    @Override
    public void clear(int index) {
        Link link = usedMap.remove(index);
        giveBackFreeLink(link);
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
        List<Integer> range = new ArrayList<>();

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
        Link firstLink = new Link(0, this.minSizeThreshold, null, null);
        getFreeLinkCollection(firstLink).add(firstLink);
    }

    private Collection<Link> getFreeLinkCollection(final Link link) {
        final int size = link.length - 1;
        return getFreeLinkCollection(size);
    }

    private Collection<Link> getFreeLinkCollection(final int size) {
        return freeMap.ceilingEntry(size).getValue();
    }

    /**
     * give back the link to freeMap
     *
     * @param link
     */
    private void giveBackFreeLink(Link link) {
        // insert
        freeMap.floorEntry(link.length - 1).getValue().add(link);
        // mark as empty
        link.length = EMPTY_LENGTH;
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
