package cn.lichengwu.imemory.core.service;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.core.exception.PersistenceException;
import cn.lichengwu.imemory.core.manager.MemoryManager;
import cn.lichengwu.imemory.serializer.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-16 3:20 PM
 * @see MemoryService
 */
public class DefaultMemoryService<K, V> implements MemoryService<K, V> {

    private Class<V> valueClass;

    private Serializer serializer;

    private MemoryManager memoryManager;

    private Map<Integer, Long> indexMap = new ConcurrentHashMap<Integer, Long>();

    public DefaultMemoryService(Config config, Class<V> valueClass) {
        this.valueClass = valueClass;
        this.serializer = config.getSerializer();
        this.memoryManager = new MemoryManager(config);
    }

    @Override
    public void put(K key, V value) throws PersistenceException {
        try {
            byte [] data = serializer.serialize(value);
            long pointer = memoryManager.insert(data);
            indexMap.put(hash(key), pointer);
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public V del(K key) throws PersistenceException {
        try {
            int hash = hash(key);
            Long pointer = indexMap.get(hash);
            if (pointer == null) {
                return null;
            }
            byte[] data = memoryManager.get(pointer);
            V oldValue = serializer.deserialize(data, valueClass);
            indexMap.remove(hash);
            return oldValue;
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public V get(K key) throws PersistenceException {
        try {
            Long pointer = indexMap.get(hash(key));
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

    private int hash(Object key) {
        return key.hashCode();
    }

}
