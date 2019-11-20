package com.paytm.digital.education.application.cache;

import com.paytm.digital.education.service.CacheLockStrategy;
import com.paytm.digital.education.service.impl.CompleteLockStrategy;
import com.paytm.digital.education.service.impl.RedisOrchestratorImpl;
import com.paytm.digital.education.service.impl.WriteLockStrategy;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.CombinableMatcher.either;
import static org.hamcrest.core.Is.is;
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
    private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);

    @Autowired
    private TestService testService;

    @Autowired
    private RedisOrchestratorImpl redisOrchestrator;

    @Autowired
    private CompleteLockStrategy completeLockStrategy;

    @Autowired
    private WriteLockStrategy writeLockStrategy;

    @Autowired
    private Environment env;

    @Test
    public void testBasicFunctionalityOfEduCache() {
        logger.info("Profiles- " + StringUtils.join(env.getActiveProfiles(), "-"));
        String result1 = testService.basicTest("example1", "example2");
        String result2 = testService.basicTest("example1", "example2");
        String result3 = testService.basicTest("example1", "example2");
        String result4 = testService.basicTest("example1", "example2");
        String result5 = testService.basicTest("example1", "example2");
        assertEquals(testService.getBasicCount(), 1);
        assertEquals(result1, processTwoStrings("example1", "example2"));
        assertEquals(result2, processTwoStrings("example1", "example2"));
        assertEquals(result3, processTwoStrings("example1", "example2"));
        assertEquals(result4, processTwoStrings("example1", "example2"));
        assertEquals(result5, processTwoStrings("example1", "example2"));
    }

    @Test
    public void testCacheConcurrenceForCompleteLockStrategy() throws Exception {

        final int numberOfThreads = 10000;

        CacheLockStrategy oldCacheLock =
                this.updateCacheLockStrategyInRedisOrchestrator(completeLockStrategy);

        String arg1 = "arg1";
        String arg2 = "arg2";

        Set<Callable<String>> callables = new HashSet<>();

        for (int i = 0; i < numberOfThreads; ++i) {
            callables.add(() -> testService.testString(arg1, arg2));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(MAX_VALUE, NANOSECONDS);
        assertThat(testService.getEvents().size(), greaterThan(0));
        for (int i = 0; i < testService.getEvents().size() - 1; ++i) {
            assertNotEquals(testService.getEvents().get(i), testService.getEvents().get(i + 1));
        }
        logger.info("Number of writes count (approx) - {}", testService.getTestCount());

        this.updateCacheLockStrategyInRedisOrchestrator(oldCacheLock);
    }

    @Test
    public void testCacheConcurrenceSequentialExecutionForWriteLockStrategy() throws Exception {
        final int numberOfThreads = 10000;

        CacheLockStrategy oldCacheLock =
                this.updateCacheLockStrategyInRedisOrchestrator(writeLockStrategy);
        String arg1 = "arg1";
        String arg2 = "arg2";

        Set<Callable<String>> callables = new HashSet<>();

        for (int i = 0; i < numberOfThreads; ++i) {
            callables.add(() -> testService.testString(arg1, arg2));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<String>> futures = executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(120, SECONDS);
        for (Future<String> future : futures) {
            assertTrue(future.isDone());
            assertThat(future.get(), either(is(processTwoStrings(arg1, arg2))).or(is((String) null)));
        }
        assertThat(testService.getEvents().size(), greaterThan(0));
        for (int i = 0; i < testService.getEvents().size() - 1; ++i) {
            assertNotEquals(testService.getEvents().get(i), testService.getEvents().get(i + 1));
        }
        logger.info("Number of writes count (approx) - {}", testService.getTestCount());
        this.updateCacheLockStrategyInRedisOrchestrator(oldCacheLock);
    }

    @Test
    public void testCacheConcurrenceRunsMultipleTimeWithoutCacheAnnotation() throws Exception {
        final int numberOfThreads = 1000;

        String arg1 = "arg1";
        String arg2 = "arg2";

        Set<Callable<String>> callables = new HashSet<>();

        for (int i = 0; i < numberOfThreads; ++i) {
            callables.add(() -> testService.controlMethod(arg1, arg2));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<String>> futureValues = executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(MAX_VALUE, NANOSECONDS);
        for (Future<String> future : futureValues) {
            assertTrue(future.isDone());
            assertEquals(future.get(), processTwoStrings(arg1, arg2));
        }
        assertEquals(testService.getControlCount().get(), numberOfThreads);
    }

    private CacheLockStrategy updateCacheLockStrategyInRedisOrchestrator(CacheLockStrategy newCacheLockStrategy)
            throws Exception {
        Field cacheLockStrategyField = redisOrchestrator.getClass().getDeclaredField("cacheLockStrategy");
        cacheLockStrategyField.setAccessible(true);
        CacheLockStrategy oldCacheLockStrategy = (CacheLockStrategy) cacheLockStrategyField.get(redisOrchestrator);
        cacheLockStrategyField.set(redisOrchestrator, newCacheLockStrategy);
        cacheLockStrategyField.setAccessible(false);
        return oldCacheLockStrategy;
    }
}
