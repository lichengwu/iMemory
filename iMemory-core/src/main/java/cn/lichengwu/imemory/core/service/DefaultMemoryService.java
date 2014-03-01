package cn.lichengwu.imemory.core.service;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.core.exception.PersistenceException;
import cn.lichengwu.imemory.core.manager.MemoryManager;
import cn.lichengwu.imemory.serializer.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-16 3:20 PM
 * @see MemoryService
 */
public class DefaultMemoryService<K extends Serializable, V> implements MemoryService<K, V> {

    private Class<V> valueClass;

    private Serializer serializer;

    private MemoryManager memoryManager;

    private ConcurrentMap<Integer, Long> writeFirstIndex = new ConcurrentHashMap<>();

    private ConcurrentMap<K, Long> readFirstIndex = new ConcurrentHashMap<>();

    transient final int hashSeed = sun.misc.Hashing.randomHashSeed(this);

    public DefaultMemoryService(Config config, Class<V> valueClass) {
        this.valueClass = valueClass;
        this.serializer = config.getSerializer();
        this.memoryManager = new MemoryManager(config);
    }

    @Override
    public void set(K key, V value) throws PersistenceException {
        try {
            byte[] data = serializer.serialize(value);
            long pointer = memoryManager.insert(data);
            //first write
            Long oldPointer = writeFirstIndex.putIfAbsent(hash(key), pointer);
            // if write failed, try another way
            if (oldPointer != null) {
                readFirstIndex.put(key, pointer);
            }
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public V del(K key) throws PersistenceException {
        try {
            Long pointer = removePointer(key);
            if (pointer == null) {
                return null;
            }
            byte[] data = memoryManager.get(pointer);
            return serializer.deserialize(data, valueClass);
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public V get(K key) throws PersistenceException {
        try {
            Long pointer = getPointer(key);
            if (pointer == null) {
                return null;
            }
            byte[] data = memoryManager.get(pointer);
            if (data == null) {
                return null;
            }
            return serializer.deserialize(data, valueClass);
        } catch (IOException e) {
            throw new PersistenceException(e);
        }

    }

    private Long getPointer(K key) {
        // read first
        Long pointer = readFirstIndex.get(key);
        if (pointer == null) {
            // second read
            pointer = writeFirstIndex.get(hash(key));
        }
        return pointer;
    }

    private Long removePointer(K key) {
        // remove first
        Long pointer = readFirstIndex.remove(key);
        if (pointer == null) {
            // remove read
            pointer = writeFirstIndex.remove(hash(key));
        }
        return pointer;
    }

    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because ConcurrentHashMap uses power-of-two length hash tables,
     * that otherwise encounter collisions for hashCodes that do not
     * differ in lower or upper bits.
     */
    private int hash(Object k) {
        int h = hashSeed;

        if ((0 != h) && (k instanceof String)) {
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h <<  15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h <<   3);
        h ^= (h >>>  6);
        h += (h <<   2) + (h << 14);
        return h ^ (h >>> 16);
    }

}
