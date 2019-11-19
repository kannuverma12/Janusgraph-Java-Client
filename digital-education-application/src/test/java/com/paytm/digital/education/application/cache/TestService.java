package com.paytm.digital.education.application.cache;

import com.paytm.digital.education.annotation.EduCache;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.paytm.digital.education.application.cache.TestUtil.processTwoStrings;

@Service
@Data
public class TestService {

    private int testCount;
    private AtomicInteger controlCount = new AtomicInteger();
    private List<Integer> events = new ArrayList<>();

    @EduCache(keys = {"one", "two"})
    public String testString(String one, String two) {
        events.add(0);
        ++testCount;
        String value = processTwoStrings(one, two);
        events.add(1);
        return value;
    }

    public String controlMethod(String one, String two) {
        controlCount.incrementAndGet();
        return processTwoStrings(one, two);
    }
}
