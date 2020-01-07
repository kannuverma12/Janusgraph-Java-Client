package com.paytm.digital.education.profiles.service;

import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.profiles.db.dao.CandidateProfileDAO;
import com.paytm.digital.education.profiles.db.dao.CandidateProfileDataDAO;
import com.paytm.digital.education.profiles.db.model.ProfileDataEntity;
import com.paytm.digital.education.profiles.db.model.ProfileIdentifierEntity;
import com.paytm.digital.education.profiles.request.ProfileDataRequest;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CandidateProfileDataService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateProfileDataService.class);

    private CandidateProfileDataDAO candidateProfileDataDAO;

    private CandidateProfileDAO candidateProfileDAO;

    public ProfileDataEntity updateCandidateProfileData(ProfileDataRequest profileDataRequest,
            Long profileId) {

        ProfileIdentifierEntity profileIdentifierEntity =
                candidateProfileDAO.findByProfileId(profileId).orElseThrow(() ->
                        new InvalidRequestException("profile id should be present"));

        ProfileDataEntity profileDataEntity = candidateProfileDataDAO
                .findByProfileIdAndKey(profileId, profileDataRequest.getKey()).orElse(null);

        if (profileDataEntity == null) {
            profileDataEntity = new ProfileDataEntity();
            profileDataEntity.setCustomerId(profileIdentifierEntity.getCustomerId());
            profileDataEntity.setProfileId(profileId);
            profileDataEntity.setKey(profileDataRequest.getKey());
        }

        profileDataEntity.setIsEnabled(profileDataRequest.getIsEnabled());
        profileDataEntity.setValue(profileDataRequest.getValue());

        try {
            return candidateProfileDataDAO.save(profileDataEntity);
        } catch (NonTransientDataAccessException ex) {
            logger.error("error on updating profile data", ex);
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

}
