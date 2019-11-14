package com.paytm.digital.education.application.cache;

import com.paytm.digital.education.service.impl.RedisOrchestratorImpl;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisOrchestratorImpl.class);

    @Autowired
    private TestService testService;

    private static int NUMBER_OF_THREADS = 5000;
    private static int THREAD_POOL_SIZE = 5000;
    private static double EXPECTED_FRACTION_OF_THREADS_WHICH_WROTE = 0.1 / 100;

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
         * Note:- In case of cache miss, out of 10000 concurrent requests
         * only 8 (approx) (0.08%) were able to write.
         * Ideally, only one request should have written, but to ensure that, we would
         * have to lock the whole operation (including the fetch from cache operation),
         * which would have incurred a much greater penalty.
         */
        final int EXPECTED_NUMBER_OF_THREADS_WHICH_WROTE =
                (int) (EXPECTED_FRACTION_OF_THREADS_WHICH_WROTE * NUMBER_OF_THREADS);
        assertThat(TestService.TEST_COUNT, lessThanOrEqualTo(EXPECTED_NUMBER_OF_THREADS_WHICH_WROTE));
        logger.info("Number of writes count (approx) - {}", TestService.TEST_COUNT);
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
