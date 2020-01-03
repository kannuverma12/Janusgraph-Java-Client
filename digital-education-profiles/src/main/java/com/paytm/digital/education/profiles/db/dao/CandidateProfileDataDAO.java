package com.paytm.digital.education.profiles.db.dao;

import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.profiles.db.model.ProfileDataEntity;
import com.paytm.digital.education.profiles.db.repository.CandidateProfileDataRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CandidateProfileDataDAO {

    @Autowired
    private CandidateProfileDataRepository candidateProfileDataRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public ProfileDataEntity save(@NonNull ProfileDataEntity profileDataEntity) {
        return candidateProfileDataRepository.save(profileDataEntity);
    }

    public Optional<ProfileDataEntity> findByProfileIdAndKey(@NonNull Long id, String key) {
        return Optional.ofNullable(candidateProfileDataRepository
                .findByProfileIdAndKey(id, key));
    }

    public Optional<List<ProfileDataEntity>> findByCustomerId(@NonNull Long id, Boolean isEnabled) {
        return Optional.ofNullable(
                candidateProfileDataRepository.findByCustomerIdAndIsEnabled(id, isEnabled));
    }

    public Optional<List<ProfileDataEntity>> findByProfileId(@NonNull Long id, Boolean isEnabled) {
        return Optional.ofNullable(
                candidateProfileDataRepository.findByProfileIdAndIsEnabled(id, isEnabled));
    }

    public Optional<List<ProfileDataEntity>> findByProfileIdAndKeys(@NonNull Long id,
            @NonNull List<String> keys, Boolean isEnabled) {
        return Optional.ofNullable(candidateProfileDataRepository
                .findByProfileIdAndKeyInAndIsEnabled(id, keys, isEnabled));
    }


}
