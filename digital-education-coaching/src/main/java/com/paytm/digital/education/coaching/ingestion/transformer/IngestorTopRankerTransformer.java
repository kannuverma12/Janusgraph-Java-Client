package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.TopRankerForm;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.enums.StudentCategory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestorTopRankerTransformer {

    public static TopRankerDataRequest convert(final TopRankerForm form) {
        if (null == form) {
            return null;
        }
        final TopRankerDataRequest request = TopRankerDataRequest.builder()
                .topRankerId(form.getTopRankerId())
                .instituteId(form.getInstituteId())
                .centerId(form.getCenterId())
                .examId(form.getExamId())
                .studentName(form.getStudentName())
                .studentPhoto(form.getStudentPhoto())
                .courseStudied(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getCourseIds()))
                .batchInfo(form.getYearAndBatch())
                .rankObtained(form.getRankObtained())
                .examYear(form.getExamYear())
                .collegeAdmitted(form.getCollegeAdmitted())
                .testimonial(form.getTestimonial())
                .studentCategory(StudentCategory.fromString(form.getCategory()))
                .priority(form.getGlobalPriority())
                .isEnabled(IngestorCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();

        log.info("TopRankerDataRequest: {}", request);
        return request;
    }
}
