package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.database.dao.CoachingBannerDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.database.entity.CoachingBannerEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProducerCoachingBannerService {

    @Autowired
    private CoachingBannerDAO coachingBannerDAO;

    public CoachingBannerEntity create(CoachingBannerDataRequest request) {
        CoachingBannerEntity coachingBannerEntity = new CoachingBannerEntity();
        ConverterUtil.setCoachingBannerData(request, coachingBannerEntity);
        try {
            return coachingBannerDAO.save(coachingBannerEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public CoachingBannerEntity update(CoachingBannerDataRequest request) {

        CoachingBannerEntity coachingBannerExistingEntity = Optional.ofNullable(
                coachingBannerDAO.findByCoachingBannerId(request.getCoachingBannerId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "banner id not present : " + request.getCoachingBannerId()));
        ConverterUtil.setCoachingBannerData(request, coachingBannerExistingEntity);
        try {
            return coachingBannerDAO.save(coachingBannerExistingEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }
}
