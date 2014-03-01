package cn.lichengwu.imemory.core.service;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.core.constant.StoragePolicy;
import cn.lichengwu.imemory.core.constant.StorageType;
import cn.lichengwu.imemory.core.exception.PersistenceException;
import cn.lichengwu.imemory.pojo.SimpleObject;
import cn.lichengwu.imemory.util.PojoMockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lichengwu
 * @version 1.0
 * @created 2013-11-16 9:46 PM
 */
public class DefaultMemoryServiceTest {

    private static final Logger log = LoggerFactory.getLogger(DefaultMemoryServiceTest.class);

    private Config config;

    private MemoryService<String, SimpleObject> memoryService;

    private Map<String, SimpleObject> objectMap;

    private final int SIZE = 10000;


    @Before
    public void setUp() throws Exception {
        objectMap = new HashMap<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            objectMap.put("" + i, PojoMockUtil.mockObject());
        }

    }

    @Test
    public void test() {
        try {
            // merge
            testByStoragePolicy(StoragePolicy.MERGE);
            // fix size
            testByStoragePolicy(StoragePolicy.FIX_SIZE);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * config and make a FixSizeMemoryBuffer
     */
    private void makeFixSizeMemoryService() {
        try {
            if (memoryService != null) {
                memoryService.clear();
            }
            config = new Config();
            config.setMaximum(1024 * 1024).setSliceSize(200).setStorageType(StorageType.DIRECT)
                    .setByteOrder(ByteOrder.LITTLE_ENDIAN).setStoragePolicy(StoragePolicy.FIX_SIZE);
            memoryService = new DefaultMemoryService<>(config, SimpleObject.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }


    /**
     * config and make a FixSizeMemoryBuffer
     */
    private void makeMergeMemoryService() {
        try {
            if (memoryService != null) {
                memoryService.clear();
            }
            config = new Config();
            config.setMaximum(1024 * 1024).setStorageType(StorageType.DIRECT)
                    .setByteOrder(ByteOrder.LITTLE_ENDIAN).setStoragePolicy(StoragePolicy.MERGE);
            memoryService = new DefaultMemoryService<>(config, SimpleObject.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    private void testByStoragePolicy(StoragePolicy storagePolicy) throws InterruptedException, PersistenceException {
        switch (storagePolicy) {
            case MERGE:
                makeMergeMemoryService();
                break;
            case FIX_SIZE:
                makeFixSizeMemoryService();
                break;
            default:
                Assert.fail("no match policy:" + storagePolicy);
        }

        //0. check config
        Assert.assertNotNull(config);
        Assert.assertNotNull(memoryService);

        //1. multi-put
        final int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor exec = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < nThreads; i++) {
            final int current = i;
            exec.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int sc = SIZE / nThreads;
                    for (long k = current * sc; k < Math.min(sc * (current + 1), SIZE); k++) {
                        String key = String.valueOf(k);
                        try {
                            memoryService.set(key, objectMap.get(key));
                        } catch (PersistenceException e) {
                            log.error(e.getMessage(), e);
                            Assert.fail(e.getMessage());
                        }
                    }
                    return null;
                }
            });
        }

        // waiting for task complete
        while (exec.getActiveCount() != 0) {
            TimeUnit.SECONDS.sleep(1);
        }
        exec.shutdown();

        //3. get
        for (int i = 0; i < SIZE; i++) {
            String key = String.valueOf(i);
            Assert.assertEquals(memoryService.get(key), objectMap.get(key));

        }

        //4. del
        for (int i = 0; i < SIZE; i++) {
            String key = String.valueOf(i);
            Assert.assertEquals(memoryService.del(key), objectMap.get(key));
        }

        Assert.assertEquals(memoryService.capacity(), config.getRealMaximum());
    }

}
