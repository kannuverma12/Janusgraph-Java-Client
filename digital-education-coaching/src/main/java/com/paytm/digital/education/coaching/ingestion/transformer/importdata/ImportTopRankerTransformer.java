package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.TopRankerForm;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.enums.StudentCategory;

public class ImportTopRankerTransformer {

    public static TopRankerDataRequest convert(final TopRankerForm form) {
        if (null == form) {
            return null;
        }
        return TopRankerDataRequest.builder()
                .topRankerId(form.getTopRankerId())
                .instituteId(form.getInstituteId())
                .centerId(form.getCenterId())
                .examId(form.getExamId())
                .studentName(form.getStudentName())
                .studentPhoto(form.getStudentPhoto())
                .courseStudied(ImportCommonTransformer.convertStringToListOfLong(
                        form.getCourseIds()))
                .batchInfo(form.getYearAndBatch())
                .rankObtained(form.getRankObtained())
                .examYear(form.getExamYear())
                .collegeAdmitted(form.getCollegeAdmitted())
                .testimonial(form.getTestimonial())
                .studentCategory(StudentCategory.fromString(form.getCategory()))
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }
}
