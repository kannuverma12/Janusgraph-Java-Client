package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.WIDGETS;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyList;

import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.EntitySourceMappingProvider;
import com.paytm.digital.education.serviceimpl.helper.ExamDatesHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class WidgetsEntityDataHelperTest {
    private static List<String> ONLINE = singletonList("ONLINE");
    private static List<String> OFFLINE = singletonList("OFFLINE");
    @Mock
    private CommonMongoRepository commonMongoRepository;

    @Mock
    private EntitySourceMappingProvider entitySourceMappingProvider;

    @Mock
    private StreamDAO streamDAO;

    @Mock
    private CommonEntityMongoDAO commonEntityMongoDAO;

    @Mock
    private PropertyReader propertyReader;

    @Mock
    private ExamDatesHelper examDatesHelper;

    @InjectMocks
    private WidgetsDataHelper widgetsDataHelper;

    private SimilarExamsHelper similarExamsHelper;

    @InjectMocks
    private ExamInstanceHelper examInstanceHelper;

    @Before
    public void setUp() {
        similarExamsHelper = new SimilarExamsHelper(
                widgetsDataHelper, streamDAO, commonEntityMongoDAO, examDatesHelper);
        Mockito.when(
                propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, EXAM.name().toLowerCase(),
                        WIDGETS))
                .thenReturn(getWidgetsPropertiesData());
        Mockito.when(commonEntityMongoDAO
                .getExamsByIdsIn(anyList(), anyList()))
                .thenReturn(getExamList());
        Mockito.when(streamDAO.findAll()).thenReturn(getAllStreamsEntities());
    }

    @Test
    public void getSimiliarExamsWithHigherPriorityStream() {
        Exam exam = new Exam();
        exam.setExamId(3L);
        exam.setStreamIds(Arrays.asList(1L, 2L));

        List<Widget> similarExamsWidgets = similarExamsHelper.getWidgetsData(exam);
        assertFalse(CollectionUtils.isEmpty(similarExamsWidgets));
        List<WidgetData> widgetDataList =
                similarExamsWidgets.stream().flatMap(widget -> widget.getData().stream()).collect(
                        Collectors.toList());
        // check for of higher precedence stream
        widgetDataList.forEach(widgetData -> {
            assertEquals((long) 1, (long) widgetData.getStreamId());
        });
    }

    @Test
    public void getSimiliarExamsSingleStream() {
        Exam exam = new Exam();
        exam.setExamId(4L);
        exam.setStreamIds(Arrays.asList(2L));

        List<Widget> similarExamsWidgets = similarExamsHelper.getWidgetsData(exam);
        assertFalse(CollectionUtils.isEmpty(similarExamsWidgets));
        List<WidgetData> widgetDataList =
                similarExamsWidgets.stream().flatMap(widget -> widget.getData().stream()).collect(
                        Collectors.toList());
        // check for single requested stream
        widgetDataList.forEach(widgetData -> {
            assertEquals((long) 2, (long) widgetData.getStreamId());
        });
    }

    @Test
    public void getSimiliarExamsWithDefaulStream() {
        Exam exam = new Exam();
        exam.setExamId(3L);

        List<Widget> similarExamsWidgets = similarExamsHelper.getWidgetsData(exam);
        assertFalse(CollectionUtils.isEmpty(similarExamsWidgets));
        List<WidgetData> widgetDataList =
                similarExamsWidgets.stream().flatMap(widget -> widget.getData().stream()).collect(
                        Collectors.toList());
        // check for of higher precedence stream
        widgetDataList.forEach(widgetData -> {
            assertEquals((long) 0, (long) widgetData.getStreamId());
        });
    }

    private List<Exam> getExamList() {
        List<Exam> exams = new ArrayList<>();
        List<Instance> instances = new ArrayList<>();
        Instance instance1 = getFutureInstance(123, null, 3);
        Instance instance2 = getFutureInstance(234, null, 6);
        instances.add(instance1);
        instances.add(instance2);
        Exam jeeExam = createExam(1l, "Joint Entrance Exam Mains", "JEE Mains", "/1/1.jpeg",
                Arrays.asList(1L), instances);
        exams.add(jeeExam);

        Exam bitStatExam = createExam(1882l, "BITSAT", "BITSAT", "/1882/1882.jpeg",
                Arrays.asList(1L), null);
        exams.add(bitStatExam);

        Exam nataExam = createExam(453l, "NATA", "NATA", "/453/453.jpeg",
                Arrays.asList(1L), null);
        exams.add(nataExam);

        List<Instance> instanceList = new ArrayList<>();
        Instance instance3 = getFutureInstance(564, null, 10);
        instanceList.add(instance3);
        Exam neetExam = createExam(789l, "BITSAT", "BITSAT", "/789/789.jpeg",
                Arrays.asList(2L), instanceList);
        exams.add(neetExam);

        Exam aiimsExam = createExam(959l, "AIIMS", "AIIMS", "/1882/1882.jpeg",
                Arrays.asList(2L), null);
        exams.add(aiimsExam);

        Exam matExam = createExam(777l, "Management Aptitude Test", "MAT", "/777/777.jpeg",
                Arrays.asList(3L), instanceList);
        exams.add(matExam);

        Exam catExam = createExam(126l, "Common Aptitude Test", "CAT", "/126/126.jpeg",
                Arrays.asList(3L), null);
        exams.add(catExam);
        return exams;
    }

    private Exam createExam(long examId, String fullName, String shortName, String logo,
            List<Long> streamIds, List<Instance> instances) {
        Exam newExam = new Exam();

        newExam.setExamId(examId);
        newExam.setExamFullName(fullName);
        newExam.setExamShortName(shortName);
        newExam.setLogo(logo);
        newExam.setStreamIds(streamIds);
        newExam.setInstances(instances);
        return newExam;
    }

    private List<StreamEntity> getAllStreamsEntities() {
        List<StreamEntity> streamEntities = new ArrayList<>();
        StreamEntity entity1 = new StreamEntity();
        entity1.setStreamId(1L);
        entity1.setPriority(1);
        streamEntities.add(entity1);

        StreamEntity entity2 = new StreamEntity();
        entity2.setStreamId(2L);
        entity2.setPriority(2);
        streamEntities.add(entity2);

        StreamEntity entity3 = new StreamEntity();
        entity3.setStreamId(3L);
        entity3.setPriority(3);
        streamEntities.add(entity3);

        StreamEntity entity4 = new StreamEntity();
        entity4.setStreamId(4L);
        entity4.setPriority(4);
        streamEntities.add(entity4);

        StreamEntity entity5 = new StreamEntity();
        entity5.setStreamId(5L);
        entity5.setPriority(5);
        streamEntities.add(entity5);

        StreamEntity entity6 = new StreamEntity();
        entity6.setStreamId(6L);
        entity6.setPriority(6);
        streamEntities.add(entity6);

        return streamEntities;
    }

    private Map<String, Object> getWidgetsPropertiesData() {
        List<WidgetData> widgetList = new ArrayList<>();

        WidgetData widgetData1 = new WidgetData();
        widgetData1.setEntityId(1L);
        widgetData1.setStreamId(1L);
        widgetList.add(widgetData1);

        WidgetData widgetData2 = new WidgetData();
        widgetData2.setEntityId(1882L);
        widgetData2.setStreamId(1L);
        widgetList.add(widgetData2);

        WidgetData widgetData3 = new WidgetData();
        widgetData3.setEntityId(453L);
        widgetData3.setStreamId(1L);
        widgetList.add(widgetData3);

        WidgetData widgetData4 = new WidgetData();
        widgetData4.setEntityId(789L);
        widgetData4.setStreamId(2L);
        widgetList.add(widgetData4);

        WidgetData widgetData5 = new WidgetData();
        widgetData5.setEntityId(959L);
        widgetData5.setStreamId(2L);
        widgetList.add(widgetData5);

        WidgetData defaultWidget1 = new WidgetData();
        defaultWidget1.setEntityId(959L);
        defaultWidget1.setStreamId(0L);
        widgetList.add(defaultWidget1);

        WidgetData defaultWidget2 = new WidgetData();
        defaultWidget2.setEntityId(959L);
        defaultWidget2.setStreamId(0L);
        widgetList.add(defaultWidget2);

        Widget widget = new Widget("exam", "Similar Exams", widgetList);
        List<Widget> data = new ArrayList<>();
        data.add(widget);

        Map<String, Object> outerMap = new LinkedHashMap<>();
        outerMap.put("data", data);
        return outerMap;
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
        eventWithDate.setDate(new LocalDate().plusDays(futureDays + 1).toDate());
        eventWithDate.setModes(ONLINE);
        eventWithDate.setType("ADVERTISEMENT");
        instance.getEvents().add(eventWithDate);

        Event eventDateRange = new Event();
        eventDateRange.setCertainty(NON_TENTATIVE);
        eventDateRange.setDateRangeStart(new LocalDate().plusDays(futureDays + 2).toDate());
        eventDateRange.setDateRangeEnd(new LocalDate().plusDays(futureDays + 10).toDate());
        eventDateRange.setType("APPLICATION");
        eventDateRange.setModes(ONLINE);
        instance.getEvents().add(eventDateRange);

        Event eventApplication = new Event();
        eventApplication.setCertainty(NON_TENTATIVE);
        eventApplication.setDate(new LocalDate().plusDays(futureDays - 2).toDate());
        eventApplication.setType("OTHER");
        eventApplication.setOtherEventLabel("Mock Test");
        eventApplication.setModes(ONLINE);
        instance.getEvents().add(eventApplication);

        Event eventExam = new Event();
        eventExam.setCertainty(NON_TENTATIVE);
        eventExam.setDate(new LocalDate().plusDays(futureDays + 20).toDate());
        eventExam.setType("EXAM");
        eventExam.setModes(ONLINE);
        instance.getEvents().add(eventExam);

        Event eventWithMonthDate = new Event();
        eventWithMonthDate.setCertainty(TENTATIVE);
        int month = new LocalDate().plusDays(futureDays + 20).getMonthOfYear();
        int year = new LocalDate().plusDays(futureDays + 20).getYear();
        eventWithMonthDate.setMonthDate((month < 10 ? "0" + month : month) + "-" + year);
        eventWithMonthDate.setType("RESULTS");
        eventWithMonthDate.setModes(OFFLINE);
        instance.getEvents().add(eventWithMonthDate);
        return instance;
    }


}
