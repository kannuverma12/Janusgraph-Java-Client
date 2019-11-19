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
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

    private static int NUMBER_OF_THREADS = 1000;
    private static int THREAD_POOL_SIZE = 1000;

    @Test
    public void testCacheConcurrenceSequentialExecutionForWriteLock() throws Exception {
        String arg1 = "arg1";
        String arg2 = "arg2";

        Set<Callable<String>> callables = new HashSet<>();

        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            callables.add(() -> testService.testString(arg1, arg2));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(MAX_VALUE, NANOSECONDS);
        assertThat(testService.getEvents().size(), greaterThan(0));
        for (int i = 0; i < testService.getEvents().size() - 1; ++i) {
            assertNotEquals(testService.getEvents().get(i), testService.getEvents().get(i + 1));
        }
        logger.info("Number of writes count (approx) - {}", testService.getTestCount());
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
        assertEquals(testService.getControlCount().get(), NUMBER_OF_THREADS);
    }
}
