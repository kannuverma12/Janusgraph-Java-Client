package com.paytm.digital.education.utility.service;

import com.paytm.digital.education.advice.CacheKeyable;
import com.paytm.digital.education.advice.helper.KeyGenerator;
import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.exception.UnableToAccessBeanPropertyException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.paytm.digital.education.enums.ClassType.ONE;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.utility.CommonUtils.sortMapByKeys;
import static com.paytm.digital.education.utility.JsonUtils.toJson;
import static com.paytm.digital.education.utility.enums.Test.VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorTest {
    private static final Logger log = LoggerFactory.getLogger(KeyGeneratorTest.class);

    private static final String CACHE_NAME = "test_cache";

    private KeyGenerator keyGenerator = new KeyGenerator(2000);

    @Test
    public void testDefaultArgs() {
        assertNotNull(keyGenerator);

        EduCache eduCache = getCache(CACHE_NAME, new String[0]);
        String[] params = {"param1", "param2", "param3"};
        Object[] values1 = {"value1", "value2", "value3"};
        assertEquals(
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method", params, values1),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest."
                        + "test_method.value1.value2.value3");

        Object[] values2 = {"value1", 3, KeyGeneratorTest.class};
        assertEquals(
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method", params, values2),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest.test_method."
                        + "value1.3.com.paytm.digital.education.utility.service.KeyGeneratorTest");

        ObjectId oid = new ObjectId();
        CacheKeyable cacheKeyable = new CacheKeyableExample(1, oid, 2L, "someVal");
        Object[] values3 = {"value1", 3, KeyGeneratorTest.class, cacheKeyable};
        assertEquals(
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method", params, values3),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest."
                        + "test_method.value1.3.com.paytm.digital.education.utility.service.KeyGeneratorTest"
                        + ".1." + oid.toString() + ".2.someVal");

        ObjectId oid2 = new ObjectId();
        List<Object> listOfValues = new ArrayList<>();
        listOfValues.add(7);
        listOfValues.add("17");
        listOfValues.add(new CacheKeyableExample(55, oid2, 20L, "value3"));
        Object[] values4 = {"value1", 3, KeyGeneratorTest.class, cacheKeyable, listOfValues};
        assertEquals(
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method", params, values4),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest."
                        + "test_method.value1.3.com.paytm.digital.education.utility.service.KeyGeneratorTest"
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
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method2", params, values),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest.test_method2."
                        + "com.paytm.digital.education.utility.service.KeyGeneratorTest."
                        + oid.toString());
    }

    @Test
    public void testMapKey() {
        assertNotNull(keyGenerator);

        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", "something");

        EduCache eduCache = getCache(CACHE_NAME, new String[]{});
        String[] params = {"param1"};
        Object[] values = { map };
        assertEquals(
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method", params, values),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest.test_method."
                        + "{\"a\":1,\"b\":\"something\"}");
    }

    @Test
    public void testEnumKey() {
        assertNotNull(keyGenerator);

        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", "something");

        EduCache eduCache = getCache(CACHE_NAME, new String[]{});
        String[] params = {"param1"};
        Object[] values = { VALUE };
        assertEquals(
                keyGenerator.generateKey(eduCache, KeyGeneratorTest.class, "test_method", params, values),
                "test_cache##com.paytm.digital.education.utility.service.KeyGeneratorTest.test_method.VALUE");
    }

    @Test(expected = UnableToAccessBeanPropertyException.class)
    public void exceptionWhenNoGetterDefined() {
        EduCache eduCache = getCache(CACHE_NAME, new String[]{"param1.val"});
        String[] params = {"param1"};
        Object[] values = {new CacheKeyableExample(1, new ObjectId(), 2L, "someVal")};
        keyGenerator.generateKey(eduCache, KeyGenerator.class, "test_method", params, values);
    }

    @Test
    public void sortedMapTests() {
        assertEquals(ONE.name(), "ONE");
        assertEquals(EXAM.name(),"EXAM");
        Map<String, Integer> stringObjectMap = sortMapByKeys(of("b", 1, "a", 3));
        Iterator<Map.Entry<String, Integer>> stringMapIterator = stringObjectMap.entrySet().iterator();
        assertEquals("a", stringMapIterator.next().getKey());
        assertEquals("b", stringMapIterator.next().getKey());

        Map<Sort.Direction, Integer> directionObjectMap = sortMapByKeys(of(DESC, 1, ASC, 3));
        Iterator<Map.Entry<Sort.Direction, Integer>> directionMapIterator = directionObjectMap.entrySet().iterator();
        assertEquals(ASC, directionMapIterator.next().getKey());
        assertEquals(DESC, directionMapIterator.next().getKey());
        assertEquals("{\"a\":3,\"b\":1}", toJson(stringObjectMap));
        Object o = new Object();
        Map nonComparableKeyMap = of(o, "val");
        Map nonComparableKeyMapSorted = sortMapByKeys(nonComparableKeyMap);
        assertEquals(1, nonComparableKeyMapSorted.entrySet().size());
        assertEquals("val", nonComparableKeyMapSorted.get(o));
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

            @Override
            public boolean shouldCacheNull() {
                return true;
            }
        };
    }
}
