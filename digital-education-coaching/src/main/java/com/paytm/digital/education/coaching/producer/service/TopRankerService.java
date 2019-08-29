package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.db.dao.TopRankerDAO;
import com.paytm.digital.education.coaching.exeptions.ResourceNotPresentException;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TopRankerService {

    private final TopRankerDAO topRankerDAO;

    public TopRankerEntity create(final TopRankerDataRequest request) {
        final TopRankerEntity topRankerEntity = TopRankerEntity.builder()
                .instituteId(request.getInstituteId())
                .centerId(request.getCenterId())
                .batch(request.getBatchInfo())
                .courseIds(request.getCourseStudied())
                .examId(request.getExamId())
                .examYear(request.getExamYear())
                .rankObtained(request.getRankObtained())
                .studentName(request.getStudentName())
                .studentPhoto(request.getStudentPhoto())
                .testimonial(request.getTestimonial())
                .year(request.getExamYear())
                .collegeAdmitted(request.getCollegeAdmitted())
                .build();

        try {
            return topRankerDAO.save(topRankerEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }

    }

    public TopRankerEntity update(final TopRankerDataRequest request) {

        final TopRankerEntity existingTopRankerEntity =
                Optional.ofNullable(
                        topRankerDAO.findByTopRankerId(request.getTopRankerId()))
                        .orElseThrow(() -> new ResourceNotPresentException(
                                CoachingConstants.RESOURCE_NOT_PRESENT));
        ConverterUtil.setTopRanker(request, existingTopRankerEntity);

        return topRankerDAO.save(existingTopRankerEntity);
    }
}
