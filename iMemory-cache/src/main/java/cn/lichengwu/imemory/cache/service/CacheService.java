package cn.lichengwu.imemory.cache.service;

import java.io.Closeable;

/**
 * Cache service provide an operation to persist/restore java object.
 * Also, it can clean objects in cache with particular policy, like LRU.
 *
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-06 11:58 PM
 */
public interface CacheService<K, V> extends Closeable {

    /**
     * put cache to {@link cn.lichengwu.imemory.cache.service.CacheService}
     *
     * @param k
     * @param v
     *
     * @return old vale with the key or null if not exists
     */
    V put(K k, V v);


    /**
     * remove value with the key
     * @param k
     * @return
     */
    V remove(K k);

}
