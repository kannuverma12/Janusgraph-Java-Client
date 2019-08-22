package com.paytm.digital.education.coaching.producer.service;


import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingProgramEntity;
import com.paytm.digital.education.database.repository.ProgramRepository;
import com.paytm.digital.education.coaching.producer.model.request.CoachingProgramCreateRequest;
import com.paytm.digital.education.coaching.producer.transformer.ProgramTransformer;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramTransformer programTransformer;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public Long save(CoachingProgramCreateRequest coachingProgramCreateRequest) {
        CoachingProgramEntity coachingProgramEntity =
            programTransformer.transformProgramCreateRequestToProgramEntity(
                coachingProgramCreateRequest, new CoachingProgramEntity());
        coachingProgramEntity.setProgramId(sequenceGenerator.getNextSequenceId(CoachingConstants.PROGRAM));
        coachingProgramEntity = programRepository.save(coachingProgramEntity);
        if (Objects.nonNull(coachingProgramEntity)) {
            return coachingProgramEntity.getCoachingInstituteId();
        }
        return null;
    }

    public Long update(CoachingProgramCreateRequest coachingProgramCreateRequest) {

        if (Objects.isNull(coachingProgramCreateRequest.getId())) {
            return null;
        }

        CoachingProgramEntity coachingProgramEntity = null;
        Optional<CoachingProgramEntity> programEntityOptional =
            programRepository.findByProgramId(coachingProgramCreateRequest.getId());
        if (programEntityOptional.isPresent()) {
            coachingProgramEntity = programEntityOptional.get();
            coachingProgramEntity =
                programTransformer.transformProgramCreateRequestToProgramEntity(
                    coachingProgramCreateRequest, coachingProgramEntity);
            coachingProgramEntity = programRepository.save(coachingProgramEntity);
        }
        if (Objects.nonNull(coachingProgramEntity)) {
            return coachingProgramEntity.getCoachingInstituteId();
        }
        return null;
    }
}
