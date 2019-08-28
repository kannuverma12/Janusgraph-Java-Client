package com.paytm.digital.education.coaching.producer.transformer;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.coaching.producer.model.request.CreateTopRankerRequest;
import com.paytm.digital.education.coaching.producer.model.request.UpdateTopRankerRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TopRankerTransformer {

    private final SequenceGenerator sequenceGenerator;

    public TopRankerEntity transform(final CreateTopRankerRequest request) {
        return TopRankerEntity.builder()
                .topRankerId(this.sequenceGenerator.getNextSequenceId(CoachingConstants.TOP_RANKER))
                .instituteId(request.getInstituteId())
                .centerId(request.getCenterId())
                .batch(request.getBatch())
                .programId(request.getProgramId())
                .examId(request.getExamId())
                .examYear(request.getExamYear())
                .rankObtained(request.getRankObtained())
                .studentName(request.getStudentName())
                .studentPhoto(request.getStudentPhoto())
                .testimonial(request.getTestimonial())
                .year(request.getYear())
                .collegeAdmitted(request.getCollegeAdmitted())
                .build();
    }

    public TopRankerEntity transform(
            final UpdateTopRankerRequest request, final TopRankerEntity existingTopRanker) {
        return TopRankerEntity.builder()
                .id(existingTopRanker.getId())
                .topRankerId(existingTopRanker.getTopRankerId())
                .instituteId(request.getInstituteId())
                .centerId(request.getCenterId())
                .batch(request.getBatch())
                .programId(request.getProgramId())
                .examId(request.getExamId())
                .examYear(request.getExamYear())
                .rankObtained(request.getRankObtained())
                .studentName(request.getStudentName())
                .studentPhoto(request.getStudentPhoto())
                .testimonial(request.getTestimonial())
                .year(request.getYear())
                .collegeAdmitted(request.getCollegeAdmitted())
                .build();
    }
}
