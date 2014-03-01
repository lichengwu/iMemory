package cn.lichengwu.imemory.core.service;

import cn.lichengwu.imemory.core.exception.PersistenceException;

import java.io.Serializable;

/**
 * memory access interface
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-16 8:35 PM
 */
public interface MemoryService<K extends Serializable, V> {

    /**
     * put a value to memory associate with the given key
     *
     * @param key   a key reference to value
     * @param value data store in memory
     *
     * @throws PersistenceException
     */
    void set(K key, V value) throws PersistenceException;


    /**
     * get a data from memory with the given key
     *
     * @param key a key reference to value
     *
     * @return data associate with the given key in memory, null if no data associate with key
     * @throws PersistenceException
     */
    V get(K key) throws PersistenceException;

    /**
     * remove data from memory under the given key
     *
     * @param key
     *
     * @return data associate with the given key in memory, null if no data associate with key
     * @throws PersistenceException
     */
    V del(K key) throws PersistenceException;

    /**
     * clean all data in this service
     *
     * @throws PersistenceException
     */
    void clear() throws PersistenceException;

    /**
     * @see cn.lichengwu.imemory.core.manager.MemoryManager#capacity()
     */
    long capacity();

    /**
     * @return the item count in this service
     */
    long size();
}
