package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.InstituteHighLight;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class CoachingInstituteTransformer {

    public static List<InstituteHighLight> convertInstituteHighlights(
            List<KeyHighlight> keyHighlights) {
        List<InstituteHighLight> instituteHighLightList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(keyHighlights)) {
            for (KeyHighlight keyHighlight : keyHighlights) {
                InstituteHighLight instituteHighLight = InstituteHighLight.builder()
                        .key(keyHighlight.getKey())
                        .value(keyHighlight.getValue())
                        .logo(keyHighlight.getLogo()).build();
                instituteHighLightList.add(instituteHighLight);
            }
        }
        return instituteHighLightList;
    }

    public static List<Exam> convertExamEntityToDto(List<Exam> examList,
            List<com.paytm.digital.education.database.entity.Exam> examEntityList) {
        if (!CollectionUtils.isEmpty(examEntityList)) {
            for (com.paytm.digital.education.database.entity.Exam examEntity : examEntityList) {
                Exam exam = Exam.builder().id(examEntity.getExamId())
                        .examFullName(examEntity.getExamFullName())
                        .examShortName(examEntity.getExamShortName())
                        .logo(examEntity.getLogo())
                        .conductedBy(examEntity.getConductingBody()).build();
                examList.add(exam);
            }
        }
        return examList;
    }

    public static List<Stream> convertStreamEntityToStreamDto(List<Stream> streamList,
            List<StreamEntity> streamEntityList) {
        if (!CollectionUtils.isEmpty(streamEntityList)) {
            for (StreamEntity streamEntity : streamEntityList) {
                Stream stream = Stream.builder().id(streamEntity.getStreamId())
                        .name(streamEntity.getName())
                        .logo(streamEntity.getLogo()).build();
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
                TopRanker topRanker = TopRanker.builder().id(topRankerEntity.getTopRankerId())
                        .studentName(topRankerEntity.getStudentName())
                        .image(topRankerEntity.getStudentPhoto())
                        .coachingInstituteId(topRankerEntity.getInstituteId())
                        .examName(examIdsAndNameMap.get(topRankerEntity.getExamId()))
                        .coachingCourseNames(
                                getCoachingCoursesNameFromIds(coachingCourseIdsAndNameMap,
                                        topRankerEntity))
                        .rank(topRankerEntity.getRankObtained()).build();
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
