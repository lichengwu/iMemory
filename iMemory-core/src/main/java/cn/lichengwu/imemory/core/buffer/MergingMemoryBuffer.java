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
public class MergingMemoryBuffer extends AbstractMemoryBuffer {

    private static final int DEFAULT_MIN_SIZE_THRESHOLD = 64;

    // mark the Link as empty
    private static final int EMPTY_LENGTH = -1;

    // mask the Link as removed
    private static final int REMOVED = -2;

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
    private static final int DEFAULT_SIZE_RATIO_THRESHOLD = 90;

    // a mark to last link
    private volatile Link lastLink = new Link(-1, -1, null, null);


    @Override
    public void init(Config config) {
        super.init(config);
        // init free map
        for (int size : generateFreeRange(config.getMaximum())) {
            freeMap.put(size, new LinkedList<Link>());
        }
        // for huge size free memory
        freeMap.put(Integer.MAX_VALUE, new LinkedList<Link>());
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
        SortedMap<Integer, LinkedList<Link>> tailMap = freeMap.tailMap(requiredSize, true);
        int position = root.position();
        // find from free space
        for (LinkedList<Link> links : tailMap.values()) {
            for (Iterator<Link> itr = links.iterator(); itr.hasNext(); ) {
                Link link = itr.next();
                if (link.length < 0) {
                    itr.remove();
                    continue;
                }
                if (link.length >= requiredSize) {
                    itr.remove();
                    //change to new size
                    splitIfNeeded(link, requiredSize);
                    link.length = requiredSize;
                    root.position(link.position);
                    root.put(bytes);
                    //restore position
                    root.position(position);
                    usedMap.put(link.position, link);
                    this.capacity -= requiredSize;
                    return link.position;
                }
            }
        }
        //can not find suitable free space in freeMap
        if (root.remaining() < requiredSize) {
            throw new BufferOverflowException();
        }
        //put into new position
        root.put(bytes);
        //new link
        Link link = new Link(position, requiredSize, lastLink.previous, lastLink);
        // update last link
        lastLink.previous.next = link;
        lastLink.previous = link;
        lastLink.position = root.position();
        usedMap.put(position, link);
        this.capacity -= requiredSize;
        return position;
    }

    /**
     * split link into smaller slices, if waste space greater than sizeRatioThreshold
     *
     * @param link
     * @param requiredSize
     */
    private void splitIfNeeded(Link link, int requiredSize) {
        //this link's size
        int length = link.next.position - link.position;
        if (length > minSizeThreshold && (requiredSize * 100 / length) < DEFAULT_SIZE_RATIO_THRESHOLD) {
            Link split = new Link(link.position + requiredSize, EMPTY_LENGTH, link, link.next);
            link.next = split;
            giveBackToFreeLink(split);
        }
    }

    @Override
    public byte[] readBytes(int index) {
        Link link = usedMap.get(index);
        if (link == null || link.length < 0) {
            return null;
        }
        byte[] bytes = new byte[link.length];
        // back up position
        int position = root.position();
        root.position(link.position);
        root.get(bytes);
        // restore position
        root.position(position);
        return bytes;
    }

    @Override
    public void clear(int index) {
        Link link = usedMap.remove(index);
        if (link != null) {
            this.capacity += link.length;
            giveBackToFreeLink(link);
        }
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
        root.position(this.minSizeThreshold);
        Link firstLink = new Link(0, this.minSizeThreshold, null, lastLink);
        this.lastLink.previous = firstLink;
        this.lastLink.position = this.minSizeThreshold;
        getFreeLinkCollection(firstLink.length - 1).add(firstLink);
    }

    /**
     * get free Line from free map by size
     *
     * @param size required min size
     */
    private Collection<Link> getFreeLinkCollection(final int size) {
        return freeMap.ceilingEntry(size).getValue();
    }

    /**
     * give back the link to freeMap
     *
     * @param link
     */
    private void giveBackToFreeLink(Link link) {
        // merge left and right
        Link previous = link.previous;
        Link next = link.next;

        // merge just mark as removed
        if (previous != null && previous.length == EMPTY_LENGTH && next != lastLink &&
                next.length == EMPTY_LENGTH) {
            previous.length = REMOVED;
            next.length = REMOVED;
            link.position = previous.position;
            link.next = next.next;
            link.length = next.next.position - previous.position;
        }
        // merge left
        else if (previous != null && previous.length == EMPTY_LENGTH) {
            previous.length = REMOVED;
            link.previous = previous.previous;
            link.length = next.position - previous.position;
        }
        // merge right
        else if (next != lastLink && next.length == EMPTY_LENGTH) {
            next.length = REMOVED;
            link.next = next.next;
            link.length = next.next.position - link.position;
        }
        // mark as empty
        // insert
        freeMap.ceilingEntry(link.length).getValue().add(link);
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
        volatile Link previous;
        volatile Link next;

        private Link(int position, int length, Link previous, Link next) {
            this.position = position;
            this.length = length;
            this.previous = previous;
            this.next = next;
        }
    }
}
