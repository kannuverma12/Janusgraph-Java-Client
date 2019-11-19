package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCtaDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCtaDataRequest;
import com.paytm.digital.education.database.dao.CoachingCtaDAO;
import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCtaManagerService {

    private CoachingCtaDAO ctaDAO;

    public CoachingCtaDTO insertCoachingCta(CoachingCtaDataRequest request) {

        if (Objects.nonNull(request.getCtaId())) {
            throw new com.paytm.digital.education.exception.InvalidRequestException(
                    "request should not have id : " + request.getCtaId());
        }

        CoachingCtaEntity updatedCtaEntity;
        try {
            updatedCtaEntity =
                    ctaDAO.save(ConverterUtil.toCtaEntity(request, new CoachingCtaEntity()));
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }

        return ConverterUtil.toCtaDTO(updatedCtaEntity);
    }

    public CoachingCtaDTO updateCoachingCta(CoachingCtaDataRequest request) {
        Optional.ofNullable(request.getCtaId())
                .orElseThrow(() -> new InvalidRequestException("cta id should be present"));

        CoachingCtaEntity existingStreamEntity =
                Optional.ofNullable(ctaDAO.findByCtaId(request.getCtaId()))
                        .orElseThrow(() -> new InvalidRequestException(
                                "cta id not present : " + request.getCtaId()));
        ConverterUtil.toCtaEntity(request, existingStreamEntity);
        CoachingCtaEntity updatedCtaEntity;
        try {
            updatedCtaEntity =
                    ctaDAO.save(ConverterUtil.toCtaEntity(request, existingStreamEntity));
        } catch (NonTransientDataAccessException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }

        return ConverterUtil.toCtaDTO(updatedCtaEntity);
    }

    public CoachingCtaEntity getFinalCTA(@NonNull Long ctaId, @NonNull Map<String, Object> uriData)
            throws MalformedURLException, URISyntaxException {

        CoachingCtaEntity existingCtaEntity = ctaDAO.findByCtaId(ctaId);

        if (existingCtaEntity == null) {
            return null;
        }

        List<String> requiredKeys = existingCtaEntity.getProperties();
        URIBuilder data = new URIBuilder(existingCtaEntity.getUrl());
        for (String key : requiredKeys) {
            data.addParameter(key, uriData.get(key).toString());
        }
        existingCtaEntity.setUrl(data.build().toString());
        return existingCtaEntity;
    }

    public boolean isValidCTAIds(List<Long> ids) {
        List<Long> existingCTAIds = ctaDAO.findAllByCtaIdIn(ids)
                .stream().map(CoachingCtaEntity::getCtaId).collect(Collectors.toList());
        List<Long> invalidCTAds = ids.stream().filter(id -> !existingCTAIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidCTAds.isEmpty()) {
            throw new InvalidRequestException(
                    "Invalid CTA ids given : " + invalidCTAds);
        }
        return true;
    }

}
