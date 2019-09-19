package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.TopRankerForm;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportTopRankerTransformer {

    public static List<TopRankerForm> convert(final List<TopRankerEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> TopRankerForm.builder()
                        .topRankerId(entity.getTopRankerId())
                        .instituteId(entity.getInstituteId())
                        .centerId(entity.getCenterId())
                        .examId(entity.getExamId())
                        .studentName(entity.getStudentName())
                        .studentPhoto(entity.getStudentPhoto())
                        .courseIds(entity.getCourseIds() == null
                                ? EMPTY_STRING : StringUtils.join(entity.getCourseIds(), ","))
                        .yearAndBatch(entity.getBatch())
                        .rankObtained(entity.getRankObtained())
                        .examYear(entity.getExamYear())
                        .collegeAdmitted(entity.getCollegeAdmitted())
                        .testimonial(entity.getTestimonial())
                        .category(entity.getStudentCategory())
                        .globalPriority(entity.getPriority())
                        .statusActive(ExportCommonTransformer.convertBooleanToString(
                                entity.getIsEnabled()))
                        .build())
                .collect(Collectors.toList());
    }
}
