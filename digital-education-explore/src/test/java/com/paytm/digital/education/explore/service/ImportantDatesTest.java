package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.SubExam;
import com.paytm.digital.education.dto.detail.ImportantDate;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamDatesHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.TENTATIVE;

@RunWith(MockitoJUnitRunner.class)
public class ImportantDatesTest {

    @InjectMocks
    private ExamDatesHelper examDatesHelper;

    @InjectMocks
    private ExamInstanceHelper examInstanceHelper;

    @Mock
    private PropertyReader propertyReader;

    private static List<String> ONLINE = new ArrayList<>(Arrays.asList("ONLINE"));
    private static List<String> OFFLINE = new ArrayList<>(Arrays.asList("OFFLINE"));

    @Before
    public void init() {
        ReflectionTestUtils.setField(this.examInstanceHelper, "propertyReader", propertyReader);
        ReflectionTestUtils.setField(this.examDatesHelper, "instanceHelper", examInstanceHelper);
    }

    @Test
    public void nearestFutureInstancesOnly() {
        Exam exam = new Exam();
        exam.setExamFullName("MAT Test Exam");
        exam.setExamShortName("MAT");

        Instance instance1 = getFutureInstance(1, null, 10);
        Instance instance2 = getFutureInstance(3, null, 15);
        Instance instance3 = getFutureInstance(4, null, 30);
        Instance pastInstance = getInstanceWithPastDate(5, null, 20);
        List<Instance> instances = new ArrayList<>();
        instances.add(instance1);
        instances.add(instance2);
        instances.add(instance3);
        instances.add(pastInstance);
        exam.setInstances(instances);

        List<ImportantDate> importantDates = examDatesHelper.getImportantDates(exam, 2);
        Assert.assertTrue(importantDates != null && importantDates.size() == 2);
        importantDates.forEach(importantDate -> {
            Assert.assertTrue(!CollectionUtils.isEmpty(importantDate.getUpcomingDates()));
        });
    }

    @Test
    public void nearestFutureAndPastInstances() {
        Exam exam = new Exam();
        exam.setExamFullName("MAT Test Exam");
        exam.setExamShortName("MAT");

        Instance instance1 = getFutureInstance(1, null, 10);
        Instance onGoingInstance = getOngoingInstance(5, null, 2);
        Instance pastInstance1 = getInstanceWithPastDate(2, null, 10);
        Instance pastInstance2 = getInstanceWithPastDate(3, null, 15);
        List<Instance> instances = new ArrayList<>();
        instances.add(onGoingInstance);
        instances.add(instance1);
        instances.add(pastInstance1);
        instances.add(pastInstance2);
        exam.setInstances(instances);

        List<ImportantDate> importantDates = examDatesHelper.getImportantDates(exam, 2);

        Assert.assertTrue(importantDates != null && importantDates.size() == 2);
        Assert.assertTrue(!CollectionUtils.isEmpty(importantDates.get(0).getUpcomingDates()));
        Assert.assertTrue(!CollectionUtils.isEmpty(importantDates.get(0).getPastDates()));
    }

    @Test
    public void nearestPastInstancesOnly() {
        Exam exam = new Exam();
        exam.setExamFullName("MAT Test Exam");
        exam.setExamShortName("MAT");

        Instance pastInstance1 = getInstanceWithPastDate(1, null, 10);
        Instance pastInstance2 = getInstanceWithPastDate(3, null, 15);
        Instance pastInstance3 = getInstanceWithPastDate(4, null, 30);
        List<Instance> instances = new ArrayList<>();
        instances.add(pastInstance1);
        instances.add(pastInstance2);
        instances.add(pastInstance3);
        exam.setInstances(instances);

        List<ImportantDate> importantDates = examDatesHelper.getImportantDates(exam, 2);
        Assert.assertTrue(importantDates != null && importantDates.size() == 2);
        importantDates.forEach(importantDate -> {
            Assert.assertTrue(!CollectionUtils.isEmpty(importantDate.getPastDates()));
        });
    }

    @Test
    public void instancesAndSubExamsInstances() {
        Exam exam = new Exam();
        exam.setExamFullName("MAT Test Exam");
        exam.setExamShortName("MAT");

        Instance instance1 = getFutureInstance(5, null, 10);
        Instance onGoingInstance = getOngoingInstance(6, null, 2);
        List<Instance> instances = new ArrayList<>();
        instances.add(onGoingInstance);
        instances.add(instance1);
        exam.setInstances(instances);

        ArrayList<SubExam> subExams = new ArrayList<>();
        SubExam subExam1 = new SubExam();
        subExam1.setId(10L);
        subExam1.setSubExamName("Paper1");
        subExam1.setInstances(new ArrayList<>());
        Instance childInstance1 = getFutureInstance(10, 5, 6);
        Instance childInstance2 = getOngoingInstance(12, null, 4);
        Instance childInstance3 = getFutureInstance(11, null, 10);

        subExam1.getInstances().add(childInstance1);
        subExam1.getInstances().add(childInstance2);
        subExam1.getInstances().add(childInstance3);
        subExams.add(subExam1);

        exam.setSubExams(subExams);
        List<ImportantDate> importantDates = examDatesHelper.getImportantDates(exam, 2);

        Assert.assertTrue(importantDates != null && importantDates.size() == 2);
        importantDates.forEach(importantDate -> {
            Assert.assertTrue(!CollectionUtils.isEmpty(importantDate.getUpcomingDates()));
        });
    }

    @Test
    public void subExamsInstancesOnly() {
        Exam exam = new Exam();
        exam.setExamFullName("MAT Test Exam");
        exam.setExamShortName("MAT");

        ArrayList<SubExam> subExams = new ArrayList<>();
        //Adding first subexams
        SubExam subExam1 = new SubExam();
        subExam1.setId(10L);
        subExam1.setSubExamName("Paper1");
        subExam1.setInstances(new ArrayList<>());
        Instance instance1 = getFutureInstance(10, null, 6);
        Instance instance2 = getOngoingInstance(12, null, 4);

        subExam1.getInstances().add(instance1);
        subExam1.getInstances().add(instance2);
        subExams.add(subExam1);

        //Add 2nd subexams
        SubExam subExam2 = new SubExam();
        subExam2.setId(15L);
        subExam2.setSubExamName("Paper1");
        subExam2.setInstances(new ArrayList<>());
        Instance instance3 = getFutureInstance(28, null, 10);
        Instance instance4 = getFutureInstance(35, null, 12);

        subExam2.getInstances().add(instance3);
        subExam2.getInstances().add(instance4);
        subExams.add(subExam2);

        //Add 3rd subexams
        SubExam subExam3 = new SubExam();
        subExam3.setId(20L);
        subExam3.setSubExamName("Paper1");
        subExam3.setInstances(new ArrayList<>());
        Instance instance5 = getFutureInstance(40, null, 13);
        Instance instance6 = getFutureInstance(70, null, 20);

        subExam3.getInstances().add(instance5);
        subExam3.getInstances().add(instance6);
        subExams.add(subExam3);

        exam.setSubExams(subExams);
        List<ImportantDate> importantDates = examDatesHelper.getImportantDates(exam, 2);

        Assert.assertTrue(importantDates != null && importantDates.size() == 2);
        importantDates.forEach(importantDate -> {
            Assert.assertTrue(!CollectionUtils.isEmpty(importantDate.getUpcomingDates()));
        });
    }

    private Instance getInstanceWithPastDate(int instaceId, Integer parentInstanceId, int pastDays) {
        Instance instance = new Instance();
        instance.setAdmissionYear(LocalDate.now().getYear()-1);
        instance.setInstanceName("Past Instance " + instance.getAdmissionYear());
        instance.setInstanceId(instaceId);
        instance.setEvents(new ArrayList<>());

        Event eventWithDate = new Event();
        eventWithDate.setCertainty(NON_TENTATIVE);
        eventWithDate.setDate(new LocalDate().minusDays(pastDays+2).toDate());
        eventWithDate.setModes(ONLINE);
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().minusDays(pastDays+10).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().minusDays(pastDays-3).toDate());
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventApplication = new Event();
        eventApplication.setCertainty(NON_TENTATIVE);
        eventApplication.setDate(new LocalDate().plusDays(pastDays+50).toDate());
        eventApplication.setType("APPLICATION");
        eventApplication.setModes(ONLINE);
        instance.getEvents().add(eventApplication);

        Event eventExam = new Event();
        eventExam.setCertainty(NON_TENTATIVE);
        eventExam.setDate(new LocalDate().plusDays(pastDays-100).toDate());
        eventExam.setType("EXAM");
        eventExam.setModes(ONLINE);
        instance.getEvents().add(eventExam);

        return instance;
    }

    private Instance getFutureInstance(int instaceId, Integer parentInstanceId, int futureDays) {
        Instance instance = new Instance();
        instance.setAdmissionYear(LocalDate.now().plusDays(35).getYear());
        instance.setInstanceName("Future Instance " + instance.getAdmissionYear());
        instance.setInstanceId(instaceId);
        instance.setEvents(new ArrayList<>());
        instance.setParentInstanceId(parentInstanceId);

        Event eventWithDate = new Event();
        eventWithDate.setCertainty(NON_TENTATIVE);
        eventWithDate.setDate(new LocalDate().plusDays(futureDays+1).toDate());
        eventWithDate.setModes(ONLINE);
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().plusDays(futureDays+2).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().plusDays(futureDays+10).toDate());
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventApplication = new Event();
        eventApplication.setCertainty(NON_TENTATIVE);
        eventApplication.setDate(new LocalDate().plusDays(futureDays-2).toDate());
        eventApplication.setType("APPLICATION");
        eventApplication.setModes(ONLINE);
        instance.getEvents().add(eventApplication);

        Event eventExam = new Event();
        eventExam.setCertainty(NON_TENTATIVE);
        eventExam.setDate(new LocalDate().plusDays(futureDays+20).toDate());
        eventExam.setType("EXAM");
        eventExam.setModes(ONLINE);
        instance.getEvents().add(eventExam);


        Event eventWithMonthDate = new Event();
        eventWithMonthDate.setCertainty(TENTATIVE);
        int month = new LocalDate().plusDays(futureDays+20).getMonthOfYear();
        int year = new LocalDate().plusDays(futureDays+20).getYear();
        eventWithMonthDate.setMonthDate((month<10?"0"+month:month)+"-"+year);
        eventWithMonthDate.setType("RESULTS");
        eventWithMonthDate.setModes(OFFLINE);
        instance.getEvents().add(eventWithMonthDate);
        return instance;
    }

    private Instance getOngoingInstance(int instaceId, Integer parentInstanceId, int futureDays) {
        Instance instance = new Instance();
        instance.setAdmissionYear(LocalDate.now().getYear()-1);
        instance.setInstanceName("Ongoing Instance " + instance.getAdmissionYear());
        instance.setInstanceId(instaceId);
        instance.setEvents(new ArrayList<>());

        Event eventWithDate = new Event();
        eventWithDate.setCertainty(NON_TENTATIVE);
        eventWithDate.setDate(new LocalDate().plusDays(futureDays).toDate());
        eventWithDate.setType("APPLICATION");
        eventWithDate.setModes(ONLINE);
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().minusDays(3).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().plusDays(5).toDate());
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventApplication = new Event();
        eventApplication.setCertainty(NON_TENTATIVE);
        eventApplication.setDate(new LocalDate().plusDays(futureDays-20).toDate());
        eventApplication.setType("APPLICATION");
        eventApplication.setModes(ONLINE);
        instance.getEvents().add(eventApplication);

        Event eventExam = new Event();
        eventExam.setCertainty(NON_TENTATIVE);
        eventExam.setDate(new LocalDate().plusDays(futureDays+20).toDate());
        eventExam.setType("EXAM");
        eventExam.setModes(ONLINE);
        instance.getEvents().add(eventExam);

        return instance;
    }

}
