package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Base;
import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.INSTANCES;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.OTHER;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_EXAMS;
import static com.paytm.digital.education.constant.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.utility.DateUtil.dateToString;
import static com.paytm.digital.education.utility.DateUtil.formatDateString;

@Service
@AllArgsConstructor
public class SimilarExamsHelper {

    private static final Logger log = LoggerFactory.getLogger(SimilarExamsHelper.class);

    private static List<String> examProjectionFields =
            Arrays.asList(EXAM_ID, EXAM_FULL_NAME, EXAM_SHORT_NAME, LOGO, INSTANCES, SUB_EXAMS);

    private final WidgetsDataHelper     widgetsDataHelper;
    private final StreamDAO             streamDAO;
    private final ExamInstanceHelper    examInstanceHelper;
    private final CommonEntityMongoDAO  commonEntityMongoDAO;

    @Cacheable(value = "similar_exams_widgets", key = "'similar_exams.'+#exam.examId")
    public List<Widget> getWidgetsData(Exam exam) {
        Long requiredStreamId = getSimilarStreamId(exam.getStreamIds());
        List<Widget> widgets = widgetsDataHelper
                .getWidgets(EXAM.name().toLowerCase(), exam.getExamId(), requiredStreamId);
        return buildSimilarExamResponse(widgets);
    }

    private List<Widget> buildSimilarExamResponse(List<Widget> widgets) {
        if (!CollectionUtils.isEmpty(widgets)) {
            for (Widget widget : widgets) {
                if (CollectionUtils.isEmpty(widget.getData())) {
                    continue;
                }

                List<Long> entities =
                        widget.getData().stream().map(widgetData -> widgetData.getEntityId())
                                .collect(Collectors.toList());
                Map<Long, Exam> examEntityMap = getExamEntityMap(entities);
                for (WidgetData widgetData : widget.getData()) {
                    if (examEntityMap.containsKey(widgetData.getEntityId())) {
                        Exam exam = examEntityMap.get(widgetData.getEntityId());
                        widgetData.setFullName(exam.getExamFullName());
                        widgetData.setOfficialName(exam.getExamShortName());
                        widgetData.setUrlDisplayKey(CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
                        if (StringUtils.isNotBlank(exam.getLogo())) {
                            widgetData.setLogoUrl(CommonUtil.getLogoLink(exam.getLogo(), EXAM));
                        }
                        widgetData.setImportantDates(getImportantDates(exam));
                    } else {
                        log.warn("Exam Id : {} not present in database for similar exams.", widgetData.getEntityId());
                    }
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

    @Cacheable(value = "exam_domain_list", key = "'all_paytm_streams'")
    public List<Long> getAllStreamsSortedByPriority() {
        List<StreamEntity> streamEntities = streamDAO.findAll();
        return streamEntities.stream().sorted(Comparator.comparingInt(Base::getPriority))
                .map(streamEntity -> streamEntity.getStreamId()).collect(
                        Collectors.toList());
    }

    private Map<Long, Exam> getExamEntityMap(List<Long> entityIds) {
        List<Exam> exams = commonEntityMongoDAO.getExamsByIdsIn(entityIds, examProjectionFields);
        return Optional.ofNullable(exams).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(Exam::getExamId, Function
                        .identity()));
    }

    private Map<String, String> getImportantDates(Exam exam) {
        try {
            Optional<Instance> nearestInstanceOptional =
                    examInstanceHelper.getNearestInstance(exam.getInstances());
            if (nearestInstanceOptional.isPresent()) {
                Instance instance = nearestInstanceOptional.get();
                Date presentDate = new Date();
                if (!CollectionUtils.isEmpty(instance.getEvents())) {
                    Collections.sort(instance.getEvents(), Comparator.comparing(
                            Event::calculateCorrespondingDate));
                    Map<String, String> eventsMap = new LinkedHashMap<>();
                    for (Event event : instance.getEvents()) {
                        if (CommonUtils.isDateEqualsOrAfter(event.calculateCorrespondingDate(),
                                presentDate)) {
                            String eventName =
                                    OTHER.equalsIgnoreCase(event.getType()) && StringUtils
                                            .isNotBlank(event.getOtherEventLabel())
                                            ? event.getOtherEventLabel()
                                            : CommonUtil.toCamelCase(event.getType());
                            eventsMap.put(eventName, getEventDate(event));
                        }
                    }
                    return eventsMap;
                }
            }
        } catch (Exception ex) {
            log.error(
                    "Error caught while calculating important dates of similar exams. ExamId : {} ",
                    ex, exam.getExamId());
        }
        return null;
    }

    private String getEventDate(Event event) {
        if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainty())) {
            return dateToString(
                    Objects.nonNull(event.getDate()) ? event.getDate() : event.getDateRangeStart(),
                    DD_MMM_YYYY);
        }
        return formatDateString(event.getMonthDate(), YYYY_MM, MMM_YYYY) + "(Tentative)";
    }
}
