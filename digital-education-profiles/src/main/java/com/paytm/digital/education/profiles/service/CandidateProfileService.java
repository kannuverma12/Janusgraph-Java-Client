package com.paytm.digital.education.profiles.service;

import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.profiles.db.dao.CandidateProfileDAO;
import com.paytm.digital.education.profiles.db.dao.CandidateProfileDataDAO;
import com.paytm.digital.education.profiles.db.model.ProfileDataEntity;
import com.paytm.digital.education.profiles.db.model.ProfileIdentifierEntity;
import com.paytm.digital.education.profiles.request.ProfileCreateRequest;
import com.paytm.digital.education.profiles.response.ProfileDataResponse;
import com.paytm.digital.education.profiles.util.Converter;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CandidateProfileService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateProfileService.class);

    private CandidateProfileDAO candidateProfileDAO;

    private CandidateProfileDataDAO candidateProfileDataDAO;

    public ProfileIdentifierEntity saveCandidateInfo(ProfileCreateRequest profileCreateRequest) {
        ProfileIdentifierEntity profileIdentifierEntity = new ProfileIdentifierEntity();
        profileIdentifierEntity.setName(profileCreateRequest.getName());
        profileIdentifierEntity.setCustomerId(profileCreateRequest.getCustomerId());
        profileIdentifierEntity.setDateOfBirth(profileCreateRequest.getDateOfBirth());
        profileIdentifierEntity.setIsEnabled(true);
        try {
            return candidateProfileDAO.save(profileIdentifierEntity);
        } catch (NonTransientDataAccessException ex) {
            logger.error("error on saving profile", ex);
            throw new InvalidRequestException("error on saving profile", ex);
        }
    }

    public ProfileDataResponse getCandidateInfo(final Long profileId, final List<String> keys) {
        ProfileIdentifierEntity profileIdentifierEntity =
                candidateProfileDAO.findByProfileId(profileId)
                        .orElseThrow(() -> new InvalidRequestException(
                                "profile id not present : " + profileId));

        Optional<List<ProfileDataEntity>> profileDataEntities =
                Optional.ofNullable(keys).map(data -> candidateProfileDataDAO
                        .findByProfileIdAndKeys(profileId, data, true))
                        .orElse(candidateProfileDataDAO.findByProfileId(profileId, true));

        ProfileDataResponse profileDataResponse =
                Converter.getProfileDataResponse(profileDataEntities.orElse(Arrays.asList()),
                        Arrays.asList(profileIdentifierEntity));
        return profileDataResponse;
    }

    public ProfileDataResponse getCandidatesByCustomerId(final Long customerId) {

        List<ProfileIdentifierEntity> profileIdentifierEntities =
                candidateProfileDAO.findByCustomerId(customerId)
                        .orElseThrow(() -> new InvalidRequestException(
                                "customer id not present : " + customerId));

        Optional<List<ProfileDataEntity>> profileDataEntities =
                candidateProfileDataDAO.findByCustomerId(customerId, true);

        ProfileDataResponse profileDataResponse =
                Converter.getProfileDataResponse(profileDataEntities.orElse(Arrays.asList()),
                        profileIdentifierEntities);
        return profileDataResponse;
    }

    public ProfileIdentifierEntity disableProfileById(final Long profileId, Boolean status) {
        ProfileIdentifierEntity profileIdentifierEntity =
                candidateProfileDAO.findByProfileId(profileId)
                        .orElseThrow(() -> new InvalidRequestException(
                                "profile id not present : " + profileId));

        profileIdentifierEntity.setIsEnabled(status);
        try {
            return candidateProfileDAO.save(profileIdentifierEntity);
        } catch (NonTransientDataAccessException ex) {
            logger.error("error on updating profile", ex);
            throw new InvalidRequestException("error on saving profile", ex);
        }
    }
}
