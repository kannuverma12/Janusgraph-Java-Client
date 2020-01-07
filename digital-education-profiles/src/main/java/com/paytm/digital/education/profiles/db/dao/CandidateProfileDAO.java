package com.paytm.digital.education.profiles.db.dao;

import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.profiles.db.model.ProfileIdentifierEntity;
import com.paytm.digital.education.profiles.db.repository.CandidateProfileRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CandidateProfileDAO {

    @Autowired
    private CandidateProfileRepository candidateProfileRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public ProfileIdentifierEntity save(@NonNull ProfileIdentifierEntity profileIdentifierEntity) {
        if (profileIdentifierEntity.getProfileId() == null) {
            profileIdentifierEntity.setProfileId(sequenceGenerator
                    .getNextSequenceId(profileIdentifierEntity.getClass().getSimpleName()));
        }
        return candidateProfileRepository.save(profileIdentifierEntity);
    }

    public Optional<ProfileIdentifierEntity> findByProfileId(@NonNull Long id) {
        return Optional.ofNullable(candidateProfileRepository.findByProfileId(id));
    }

    public Optional<List<ProfileIdentifierEntity>> findByCustomerId(@NonNull Long id) {
        return Optional.ofNullable(candidateProfileRepository.findByCustomerId(id));
    }

}
