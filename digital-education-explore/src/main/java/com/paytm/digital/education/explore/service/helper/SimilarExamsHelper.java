package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.INSTANCES;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.RESULT;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_EXAMS;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.utility.DateUtil.dateToString;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Base;
import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SimilarExamsHelper {

    private static List<String> examProjectionFields =
            Arrays.asList(EXAM_ID, EXAM_FULL_NAME, EXAM_SHORT_NAME, LOGO, INSTANCES, SUB_EXAMS);

    private final WidgetsDataHelper     widgetsDataHelper;
    private final StreamDAO             streamDAO;
    private final CommonMongoRepository commonMongoRepository;
    private final ExamInstanceHelper    examInstanceHelper;

    public List<Widget> getWidgetsData(Exam exam) {
        Long requiredStreamId = getSimilarStreamId(exam.getStreamIds());
        List<Widget> widgets = widgetsDataHelper
                .getWidgets(EXAM.name().toLowerCase(), exam.getExamId(), requiredStreamId);
        return buildSimilarExamResponse(widgets);
    }

    private List<Widget> buildSimilarExamResponse(List<Widget> widgets) {
        if (!CollectionUtils.isEmpty(widgets)) {
            for (Widget widget : widgets) {
                List<Long> entities =
                        widget.getData().stream().map(widgetData -> widgetData.getEntityId())
                                .collect(Collectors.toList());
                Map<Long, Exam> examEntityMap = getExamEntityMap(entities);
                for (WidgetData widgetData : widget.getData()) {
                    Exam exam = examEntityMap.get(widgetData.getEntityId());
                    widgetData.setFullName(exam.getExamFullName());
                    widgetData.setOfficialName(exam.getExamShortName());
                    widgetData.setLogoUrl(CommonUtil.getLogoLink(exam.getLogo(), EXAM));
                    widgetData.setImportantDates(getImportantDates(exam));
                }
            }
        }
        return widgets;
    }

    private Long getSimilarStreamId(List<Long> streamIds) {
        if (!CollectionUtils.isEmpty(streamIds)) {
            int noOfstreams = streamIds.size();
            if (noOfstreams == 1) {
                return streamIds.get(0);
            } else {
                return findHigherPrecedenceStream(streamIds);
            }
        }
        return 0L;
    }

    /*
     ** Find the domain whose similar exams will be displayed when exam is associated with
     ** multiple domains.
     */
    private Long findHigherPrecedenceStream(List<Long> requestStreams) {
        List<Long> streamIds = getAllStreamsSortedByPriority();
        return streamIds.stream().filter(streamId -> requestStreams.contains(streamId)).findFirst()
                .orElse(0L);
    }

    @Cacheable(value = "exam_domain_list")
    public List<Long> getAllStreamsSortedByPriority() {
        List<StreamEntity> streamEntities = streamDAO.findAll();
        return streamEntities.stream().sorted(Comparator.comparingInt(Base::getPriority))
                .map(streamEntity -> streamEntity.getStreamId()).collect(
                        Collectors.toList());
    }

    private Map<Long, Exam> getExamEntityMap(List<Long> entityIds) {
        List<Exam> exams = commonMongoRepository
                .getEntityFieldsByValuesIn(EXAM_ID, entityIds, Exam.class, examProjectionFields);
        return Optional.ofNullable(exams).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(Exam::getExamId, Function
                        .identity()));
    }

    private Map<String, String> getImportantDates(Exam exam) {
        Optional<Instance> nearestInstanceOptional =
                examInstanceHelper.getNearestInstance(exam.getInstances());
        if (nearestInstanceOptional.isPresent()) {
            Instance instance = nearestInstanceOptional.get();
            if (!CollectionUtils.isEmpty(instance.getEvents())) {
                Map<String, String> eventsMap = new HashMap<>();
                for (Event event : instance.getEvents()) {
                    if (APPLICATION.equalsIgnoreCase(event.getType())) {
                        eventsMap.put("Application", getEventDate(event));
                    } else if (ExploreConstants.EXAM.equalsIgnoreCase(event.getCertainty())) {
                        eventsMap.put("Exam", getEventDate(event));
                    } else if (RESULT.equalsIgnoreCase(event.getCertainty())) {
                        eventsMap.put("Result", getEventDate(event));
                    }
                }
            }
        }
        return Collections.emptyMap();
    }

    private String getEventDate(Event event) {
        if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainty())) {
            return dateToString(
                    Objects.nonNull(event.getDate()) ? event.getDate() : event.getDateRangeStart(),
                    DD_MMM_YYYY);
        }
        return event.getMonthDate() + "(Tentative)";
    }
}
