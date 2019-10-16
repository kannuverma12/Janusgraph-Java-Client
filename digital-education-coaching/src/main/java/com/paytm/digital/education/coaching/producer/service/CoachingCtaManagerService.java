package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingCtaDAO;
import com.paytm.digital.education.coaching.exeption.InvalidRequestException;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCtaDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCtaDataRequest;
import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCtaManagerService {

    private CoachingCtaDAO ctaDAO;

    public CoachingCtaDTO insertCoachingCta(CoachingCtaDataRequest request) {

        if (request == null) {
            throw new InvalidRequestException("Can't insert null request");
        }

        CoachingCtaEntity updatedCtaEntity = ctaDAO.save(ConverterUtil.toCtaEntity(request));

        return ConverterUtil.toCtaDTO(updatedCtaEntity);
    }
}
