package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.database.entity.Event;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.property.reader.PropertyReader;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;

@RunWith(MockitoJUnitRunner.class)
public class ExamInstanceHelperTest {
    @InjectMocks
    private ExamInstanceHelper examInstanceHelper;

    @Mock
    private PropertyReader propertyReader;

    private static final String INSTANCE_1_NAME = "Test Instance 1";
    private static final String INSTANCE_2_NAME = "Test Instance 2";
    private static final String INSTANCE_3_NAME = "Test Instance 3";
    private static final String INSTANCE_4_NAME = "Test Instance 4";


    private static List<Instance> getFutureAndPastInstances() {
        Event pastEvent = new Event();
        pastEvent.setCertainty(NON_TENTATIVE);
        pastEvent.setDate(new LocalDateTime().minusMonths(1).toDate());

        Event todaysEvent = new Event();
        todaysEvent.setCertainty(NON_TENTATIVE);
        todaysEvent.setDate(new LocalDateTime().toDate());


        Event nextMonthEvent = new Event();
        nextMonthEvent.setCertainty(NON_TENTATIVE);
        nextMonthEvent.setDate(new LocalDateTime().plusMonths(1).toDate());

        Event nextMonthEventPlusTwoDays = new Event();
        nextMonthEventPlusTwoDays.setCertainty(NON_TENTATIVE);
        nextMonthEventPlusTwoDays.setDate(new LocalDateTime().plusMonths(1).plusDays(2).toDate());

        Instance instance1 = new Instance();
        instance1.setEvents(Arrays.asList(pastEvent, todaysEvent));
        instance1.setInstanceName(INSTANCE_1_NAME);

        Instance instance2 = new Instance();
        instance2.setEvents(Arrays.asList(nextMonthEvent, nextMonthEventPlusTwoDays));
        instance2.setInstanceName(INSTANCE_2_NAME);

        return Arrays.asList(instance1, instance2);
    }

    private static List<Instance> getPastInstances() {
        Event oneMonthBackEvent = new Event();
        oneMonthBackEvent.setCertainty(NON_TENTATIVE);
        oneMonthBackEvent.setDate(new LocalDateTime().minusMonths(1).toDate());

        Event twoMonthsBackEvent = new Event();
        twoMonthsBackEvent.setCertainty(NON_TENTATIVE);
        twoMonthsBackEvent.setDate(new LocalDateTime().minusMonths(2).toDate());

        Event threeMonthsBackEvent = new Event();
        threeMonthsBackEvent.setCertainty(NON_TENTATIVE);
        threeMonthsBackEvent.setDate(new LocalDateTime().minusMonths(3).toDate());

        Event fourMonthsBackEvent = new Event();
        fourMonthsBackEvent.setCertainty(NON_TENTATIVE);
        fourMonthsBackEvent.setDate(new LocalDateTime().minusMonths(4).toDate());

        Instance instance1 = new Instance();
        instance1.setEvents(Arrays.asList(oneMonthBackEvent, twoMonthsBackEvent));
        instance1.setInstanceName(INSTANCE_3_NAME);

        Instance instance2 = new Instance();
        instance2.setEvents(Arrays.asList(threeMonthsBackEvent, fourMonthsBackEvent));
        instance2.setInstanceName(INSTANCE_4_NAME);

        return Arrays.asList(instance1, instance2);
    }

    @Test
    public void shouldGetNearestFutureInstance() {
        List<Instance> instances = getFutureAndPastInstances();
        Optional<Instance> instance = examInstanceHelper.getNearestInstance(instances);
        Assert.assertTrue(instance.isPresent());
        Assert.assertEquals(instance.get().getInstanceName(), INSTANCE_1_NAME);
    }

    @Test
    public void shouldGetNearestPastInstance() {
        List<Instance> instances = getPastInstances();
        Optional<Instance> instance = examInstanceHelper.getNearestInstance(instances);
        Assert.assertTrue(instance.isPresent());
        Assert.assertEquals(instance.get().getInstanceName(), INSTANCE_3_NAME);
    }

}
