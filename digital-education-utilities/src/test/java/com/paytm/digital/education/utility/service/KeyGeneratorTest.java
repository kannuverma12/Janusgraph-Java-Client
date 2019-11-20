package com.paytm.digital.education.utility.service;

import com.paytm.digital.education.advice.CacheKeyable;
import com.paytm.digital.education.advice.helper.KeyGenerator;
import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.exception.UnableToAccessBeanPropertyException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorTest {
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);

    private static final String CACHE_NAME = "test_cache";

    private KeyGenerator keyGenerator = new KeyGenerator();

    @Autowired
    private Environment env;

    @Test
    public void testDefaultArgs() {
        logger.info("Profiles- " + StringUtils.join(env.getActiveProfiles(), "-"));

        assertNotNull(keyGenerator);

        EduCache eduCache = getCache(CACHE_NAME, new String[0]);
        String[] params = {"param1", "param2", "param3"};
        Object[] values1 = {"value1", "value2", "value3"};
        assertEquals(
                keyGenerator.generateKey(eduCache, params, values1),
                "test_cache##value1.value2.value3");

        Object[] values2 = {"value1", 3, KeyGeneratorTest.class};
        assertEquals(
                keyGenerator.generateKey(eduCache, params, values2),
                "test_cache##value1.3.com.paytm.digital.education.utility.service.KeyGeneratorTest");

        ObjectId oid = new ObjectId();
        CacheKeyable cacheKeyable = new CacheKeyableExample(1, oid, 2L, "someVal");
        Object[] values3 = {"value1", 3, KeyGeneratorTest.class, cacheKeyable};
        assertEquals(
                keyGenerator.generateKey(eduCache, params, values3),
                "test_cache##value1.3.com.paytm.digital.education.utility.service.KeyGeneratorTest"
                        + ".1." + oid.toString() + ".2.someVal");

        ObjectId oid2 = new ObjectId();
        List<Object> listOfValues = new ArrayList<>();
        listOfValues.add(7);
        listOfValues.add("17");
        listOfValues.add(new CacheKeyableExample(55, oid2, 20L, "value3"));
        Object[] values4 = {"value1", 3, KeyGeneratorTest.class, cacheKeyable, listOfValues};
        assertEquals(
                keyGenerator.generateKey(eduCache, params, values4),
                "test_cache##value1.3.com.paytm.digital.education.utility.service.KeyGeneratorTest"
                        + ".1." + oid.toString() + ".2.someVal.7.17.55." + oid2.toString() + ".20.value3");
    }

    @Test
    public void testExplicitKeys() {
        assertNotNull(keyGenerator);

        ObjectId oid = new ObjectId();
        List<Object> listOfValues = new ArrayList<>();
        listOfValues.add(7);
        listOfValues.add("17");
        CacheKeyable cacheKeyable = new CacheKeyableExample(1, oid, 2L, "someVal");

        EduCache eduCache = getCache(CACHE_NAME, new String[]{"param3", "param4.oid"});
        String[] params = {"param1", "param2", "param3", "param4", "param5"};
        Object[] values = {"value1", 3, KeyGeneratorTest.class, cacheKeyable, listOfValues};
        assertEquals(
                keyGenerator.generateKey(eduCache, params, values),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest."
                        + oid.toString());
    }

    @Test(expected = UnableToAccessBeanPropertyException.class)
    public void exceptionWhenNoGetterDefined() {
        EduCache eduCache = getCache(CACHE_NAME, new String[]{"param1.val"});
        String[] params = {"param1"};
        Object[] values = {new CacheKeyableExample(1, new ObjectId(), 2L, "someVal")};
        keyGenerator.generateKey(eduCache, params, values);
    }

    private EduCache getCache(String cacheName, String[] keys) {
        return new EduCache() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String[] keys() {
                return keys;
            }

            @Override
            public String cache() {
                return cacheName;
            }
        };
    }

}
