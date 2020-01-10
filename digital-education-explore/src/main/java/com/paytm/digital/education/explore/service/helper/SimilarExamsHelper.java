package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Base;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.serviceimpl.helper.ExamDatesHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.INSTANCES;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_EXAMS;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;

@Service
@AllArgsConstructor
public class SimilarExamsHelper {

    private static final Logger log = LoggerFactory.getLogger(SimilarExamsHelper.class);

    private static List<String> examProjectionFields =
            Arrays.asList(EXAM_ID, EXAM_FULL_NAME, EXAM_SHORT_NAME, LOGO, INSTANCES, SUB_EXAMS);

    private final WidgetsDataHelper    widgetsDataHelper;
    private final StreamDAO            streamDAO;
    private final CommonEntityMongoDAO commonEntityMongoDAO;
    private final ExamDatesHelper      examDatesHelper;

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
                        widgetData.setImportantDates(examDatesHelper.getNearestUpcomingAndOngoingImportantDates(exam));
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
}
