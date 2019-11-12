package com.paytm.digital.education.application.cache;

import com.paytm.digital.education.annotation.EduCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static com.paytm.digital.education.application.cache.TestUtil.processTwoStrings;

@Service
public class TestService {

    public static int TEST_COUNT;
    public static AtomicInteger CONTROL_COUNT = new AtomicInteger();

    @EduCache(keys = {"one", "two"})
    public String testString(String one, String two) {
        ++TEST_COUNT;
        return processTwoStrings(one, two);
    }

    public String controlMethod(String one, String two) {
        CONTROL_COUNT.incrementAndGet();
        return processTwoStrings(one, two);
    }
}
