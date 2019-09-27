package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.InstituteHighlight;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.text.WordUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_KEY_HIGHLIGHT_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TOP_RANKER_PLACEHOLDER;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_INSTITUTE_HIGHLIGHT_LOGO;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_STREAMS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_EXAMS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_RANKER;

@UtilityClass
public class CoachingInstituteTransformer {

    public static List<InstituteHighlight> convertInstituteHighlights(
            List<KeyHighlight> keyHighlights) {
        List<InstituteHighlight> instituteHighlightList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(keyHighlights)) {
            for (KeyHighlight keyHighlight : keyHighlights) {
                InstituteHighlight instituteHighLight = InstituteHighlight
                        .builder()
                        .key(keyHighlight.getKey())
                        .value(keyHighlight.getValue())
                        .logo(ImageUtils.getImageWithAbsolutePath(keyHighlight.getLogo(),
                                COACHING_INSTITUTE_KEY_HIGHLIGHT_PLACEHOLDER,
                                COACHING_INSTITUTE_HIGHLIGHT_LOGO))
                        .build();

                instituteHighlightList.add(instituteHighLight);
            }
        }
        return instituteHighlightList;
    }

    public static List<Exam> convertExamEntityToDto(List<Exam> examList,
            List<com.paytm.digital.education.database.entity.Exam> examEntityList) {
        if (!CollectionUtils.isEmpty(examEntityList)) {
            for (com.paytm.digital.education.database.entity.Exam examEntity : examEntityList) {
                Exam exam = Exam
                        .builder()
                        .id(examEntity.getExamId())
                        .examFullName(examEntity.getExamFullName())
                        .examShortName(examEntity.getExamShortName())
                        .conductedBy(examEntity.getConductingBody())
                        .logo(ImageUtils
                                .getImageWithAbsolutePath(examEntity.getLogo(), EXAM_PLACEHOLDER,
                                        COACHING_TOP_EXAMS))
                        .build();

                examList.add(exam);
            }
        }
        return examList;
    }

    public static List<Stream> convertStreamEntityToStreamDto(List<Stream> streamList,
            List<StreamEntity> streamEntityList) {
        if (!CollectionUtils.isEmpty(streamEntityList)) {
            for (StreamEntity streamEntity : streamEntityList) {
                Stream stream = Stream
                        .builder()
                        .id(streamEntity.getStreamId())
                        .name(WordUtils.capitalizeFully(streamEntity.getName()))
                        .logo(ImageUtils.getImageWithAbsolutePath(streamEntity.getLogo(),
                                STREAM_PLACEHOLDER, COACHING_STREAMS))
                        .build();

                streamList.add(stream);
            }
        }
        return streamList;
    }

    public static List<TopRanker> convertTopRankerEntityToTopRankerDto(
            Map<Long, String> examIdsAndNameMap, Map<Long, String> coachingCourseIdsAndNameMap,
            List<TopRankerEntity> topRankerEntityList, List<TopRanker> topRankerList) {
        if (!CollectionUtils.isEmpty(topRankerEntityList)) {
            for (TopRankerEntity topRankerEntity : topRankerEntityList) {
                TopRanker topRanker = TopRanker
                        .builder()
                        .id(topRankerEntity.getTopRankerId())
                        .studentName(topRankerEntity.getStudentName())
                        .coachingInstituteId(topRankerEntity.getInstituteId())
                        .coachingCentreId(topRankerEntity.getCenterId())
                        .examName(examIdsAndNameMap.get(topRankerEntity.getExamId()))
                        .coachingCourseNames(
                                getCoachingCoursesNameFromIds(coachingCourseIdsAndNameMap,
                                        topRankerEntity))
                        .rank(topRankerEntity.getRankObtained())
                        .examYear(topRankerEntity.getExamYear())
                        .image(ImageUtils
                                .getImageWithAbsolutePath(topRankerEntity.getStudentPhoto(),
                                        TOP_RANKER_PLACEHOLDER, COACHING_TOP_RANKER))
                        .testimonial(topRankerEntity.getTestimonial())
                        .build();

                topRankerList.add(topRanker);
            }
        }
        return topRankerList;
    }

    private List<String> getCoachingCoursesNameFromIds(
            Map<Long, String> coachingCourseIdsAndNameMap,
            TopRankerEntity topRankerEntity) {
        List<String> courseNameList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(topRankerEntity.getCourseIds())) {
            for (Long courseId : topRankerEntity.getCourseIds()) {
                courseNameList.add(coachingCourseIdsAndNameMap.get(courseId));
            }
        }
        return courseNameList;
    }

}
