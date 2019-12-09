package com.paytm.digital.education.application.cache;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.exception.EducationException;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.paytm.digital.education.application.cache.TestUtil.processTwoStrings;
import static com.paytm.digital.education.mapping.ErrorEnum.TEST_ENUM;

@Service
@Data
public class TestService {

    private int testCount;
    private int basicCount;
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

    @EduCache(keys = {"one", "two"})
    public String basicTest(String one, String two) {
        ++basicCount;
        return processTwoStrings(one, two);
    }

    public String controlMethod(String one, String two) {
        controlCount.incrementAndGet();
        return processTwoStrings(one, two);
    }

    @EduCache
    public String throwEducationException(String arg) {
        throw new EducationException(TEST_ENUM, new Object[]{arg});
    }

    @EduCache
    public String throwIOException(String arg) throws IOException {
        throw new IOException("Something wrong occurred " + arg);
    }
}
