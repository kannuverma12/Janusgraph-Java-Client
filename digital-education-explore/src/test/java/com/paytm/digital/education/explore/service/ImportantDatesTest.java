package com.paytm.digital.education.explore.service;

import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;

import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Instance;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class ImportantDatesTest {

    private static List<String> ONLINE = new ArrayList<>(Arrays.asList("ONLINE"));
    private static List<String> OFFLINE = new ArrayList<>(Arrays.asList("OFFLINE"));

    private Instance getInstanceWithPastDate(int instaceId) {
        Instance instance = new Instance();
        instance.setAdmissionYear(LocalDate.now().getYear()-1);
        instance.setInstanceName("Past Instance " + instance.getAdmissionYear());
        instance.setInstanceId(instaceId);
        instance.setEvents(new ArrayList<>());

        Event eventWithDate = new Event();
        eventWithDate.setCertainty(NON_TENTATIVE);
        eventWithDate.setDate(new LocalDate().minusMonths(1).toDate());
        eventWithDate.setModes(ONLINE);
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().minusMonths(1).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().minusDays(10).toDate());
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventWithMonthDate = new Event();
        eventWithMonthDate.setCertainty(NON_TENTATIVE);
        eventWithMonthDate.setDate(new LocalDate().minusMonths(1).toDate());
        eventWithMonthDate.setModes(OFFLINE);
        instance.getEvents().add(eventWithMonthDate);
        return instance;
    }

    private Instance getFutureInstance(int instaceId) {
        Instance instance = new Instance();
        instance.setAdmissionYear(LocalDate.now().getYear()-1);
        instance.setInstanceName("Future Instance " + instance.getAdmissionYear());
        instance.setInstanceId(instaceId);
        instance.setEvents(new ArrayList<>());

        Event eventWithDate = new Event();
        eventWithDate.setCertainty(NON_TENTATIVE);
        eventWithDate.setDate(new LocalDate().minusMonths(1).toDate());
        eventWithDate.setModes(ONLINE);
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().plusMonths(1).plusDays(2).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().plusMonths(1).plusDays(10).toDate());
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventWithMonthDate = new Event();
        eventWithMonthDate.setCertainty(NON_TENTATIVE);
        eventWithMonthDate.setDate(new LocalDate().minusMonths(1).toDate());
        eventWithMonthDate.setModes(OFFLINE);
        instance.getEvents().add(eventWithMonthDate);
        return instance;
    }

    private Instance getOngointInstance(int instaceId) {
        Instance instance = new Instance();
        instance.setAdmissionYear(LocalDate.now().getYear()-1);
        instance.setInstanceName("Ongoing Instance " + instance.getAdmissionYear());
        instance.setInstanceId(instaceId);
        instance.setEvents(new ArrayList<>());

        Event eventWithDate = new Event();
        eventWithDate.setCertainty(NON_TENTATIVE);
        eventWithDate.setDate(new LocalDate().minusMonths(1).toDate());
        eventWithDate.setModes(ONLINE);
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().minusMonths(1).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().minusDays(10).toDate());
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventWithMonthDate = new Event();
        eventWithMonthDate.setCertainty(NON_TENTATIVE);
        eventWithMonthDate.setDate(new LocalDate().minusMonths(1).toDate());
        eventWithMonthDate.setModes(OFFLINE);
        instance.getEvents().add(eventWithMonthDate);
        return instance;
    }

}
