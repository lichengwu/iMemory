package cn.lichengwu.imemory.core.service;

import cn.lichengwu.imemory.core.config.Config;
import cn.lichengwu.imemory.core.constant.StoragePolicy;
import cn.lichengwu.imemory.core.constant.StorageType;
import cn.lichengwu.imemory.core.exception.PersistenceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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

    private Config config;

    MemoryService<String, Long> memoryService;

    @Before
    public void setUp() {
        config = new Config().setConcurrentLevel(Runtime.getRuntime().availableProcessors())
                .setMaximum(100 * 1024 * 1024).setSliceSize(10).setStorageType(StorageType.DIRECT)
                .setStoragePolicy(StoragePolicy.MERGE);
        memoryService = new DefaultMemoryService<>(config, Long.class);
    }

    @Test
    public void test() throws InterruptedException, PersistenceException {
        Assert.assertNotNull(config);
        Assert.assertNotNull(memoryService);
        final int size = 100000;
        final int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor exec = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        List<Callable<Void>> taskList = new ArrayList<>(nThreads);
        for (int i = 0; i < nThreads; i++) {
            final int current = i;
            taskList.add(current, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int sc = size / nThreads;
                    for (long k = current * sc; k < Math.min(sc * (current + 1), size); k++) {
                        memoryService.set(String.valueOf(k), k);
                    }
                    return null;
                }
            });
        }

        exec.invokeAll(taskList);

        // waiting for task complete
        while (exec.getActiveCount() != 0) {
            TimeUnit.SECONDS.sleep(1);
        }

        for (Long i = 0L; i < size; i++) {
            Assert.assertEquals(memoryService.get(String.valueOf(i)), i);

        }

        for (Long i = 0L; i < size; i++) {
            Assert.assertEquals(memoryService.del(String.valueOf(i.toString())), i);
        }
    }

    @Test
    public void test_ins_get() {
        try {
            int size = 10;
            for (long i = 0; i < size; i++) {
                memoryService.set("" + i, i);
            }
            for (Long i = 0L; i < size; i++) {
                Assert.assertEquals(memoryService.get("" + i), i);
            }
        } catch (PersistenceException e) {
            Assert.fail();
        }
    }

}
