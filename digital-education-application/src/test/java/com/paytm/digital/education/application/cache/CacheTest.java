package com.paytm.digital.education.application.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.paytm.digital.education.application.cache.TestUtil.processTwoStrings;
import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@RunWith(SpringRunner.class)
public class CacheTest {

    @Autowired
    private TestService testService;

    private static int NUMBER_OF_THREADS = 10000;
    private static int THREAD_POOL_SIZE = 10000;

    @Test
    public void testCacheConcurrenceRunsOnlyOnce() throws Exception {
        String arg1 = "arg1";
        String arg2 = "arg2";

        Set<Callable<String>> callables = new HashSet<>();

        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            callables.add(() -> testService.testString(arg1, arg2));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<String>> futureValues = executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(MAX_VALUE, NANOSECONDS);
        for (Future<String> future : futureValues) {
            assertTrue(future.isDone());
            assertEquals(future.get(), processTwoStrings(arg1, arg2));
        }

        /*
         * Note:- In case of cache miss, out of 1000 concurrent requests
         * only 10 (1%) were able to write.
         * Ideally, only one request should have written, but to ensure that, we would
         * have to lock the whole operation (including the fetch from cache operation),
         * which would have incurred a much greater penalty.
         */
        assertThat(TestService.TEST_COUNT, lessThanOrEqualTo(NUMBER_OF_THREADS / 500));
        for (Future<String> future : futureValues) {
            assertTrue(future.isDone());
            assertEquals(future.get(), processTwoStrings(arg1, arg2));
        }
    }

    @Test
    public void testCacheConcurrenceRunsMultipleTimeWithoutCacheAnnotation() throws Exception {
        String arg1 = "arg1";
        String arg2 = "arg2";

        Set<Callable<String>> callables = new HashSet<>();

        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            callables.add(() -> testService.controlMethod(arg1, arg2));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<String>> futureValues = executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(MAX_VALUE, NANOSECONDS);
        for (Future<String> future : futureValues) {
            assertTrue(future.isDone());
            assertEquals(future.get(), processTwoStrings(arg1, arg2));
        }
        assertEquals(TestService.CONTROL_COUNT.get(), NUMBER_OF_THREADS);
    }
}
