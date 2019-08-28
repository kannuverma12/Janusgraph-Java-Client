package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.CoachingProgramEntity;
import com.paytm.digital.education.database.repository.CoachingExamRepositoryNew;
import com.paytm.digital.education.database.repository.CoachingInstituteRepositoryNew;
import com.paytm.digital.education.database.repository.CoachingProgramRepository;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamCreateRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamUpdateRequest;
import com.paytm.digital.education.coaching.producer.transformer.CoachingExamTransformer;
import com.paytm.digital.education.database.entity.Stream;
import com.paytm.digital.education.database.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingExamServiceNew {
    @Autowired
    private CoachingExamRepositoryNew      coachingExamRepositoryNew;
    @Autowired
    private CoachingInstituteRepositoryNew coachingInstituteRepositoryNew;
    @Autowired
    private CoachingProgramRepository      coachingProgramRepository;
    @Autowired
    private StreamRepository               streamRepository;
    @Autowired
    private CoachingExamTransformer        coachingExamTransformer;

    public CoachingExamEntity insertCoachingExam(CoachingExamCreateRequest request) {
        Optional<CoachingInstituteEntity> coachingInstituteEntityOptional =
                coachingInstituteRepositoryNew.findByInstituteId(request.getInstituteId());

        if (!coachingInstituteEntityOptional.isPresent()) {
            // TODO : throw exception
            return null;
        }

        if (!Objects.isNull(request.getProgramId())) {
            CoachingProgramEntity coachingProgram =
                    coachingProgramRepository.findByProgramId(request.getProgramId()).orElse(null);

            if (Objects.isNull(coachingProgram)) {
                // TODO : throw exception
                return null;
            }
        }

        if (!Objects.isNull(request.getStreamId())) {
            Stream stream = streamRepository.findByStreamId(request.getStreamId());

            if (Objects.isNull(stream)) {
                // TODO : throw exception
                return null;
            }
        }

        CoachingExamEntity toSave = coachingExamTransformer.transform(request);
        return coachingExamRepositoryNew.save(toSave);
    }

    public CoachingExamEntity updateCoachingExam(CoachingExamUpdateRequest request) {
        CoachingExamEntity existingCoachingExam =
                coachingExamRepositoryNew.findByCoachingExamId(request.getCoachingExamId())
                        .orElse(null);

        if (Objects.isNull(existingCoachingExam)) {
            // TODO : throw exception
            return null;
        }

        Optional<CoachingInstituteEntity> coachingInstituteEntityOptional =
                coachingInstituteRepositoryNew.findByInstituteId(request.getInstituteId());

        if (!coachingInstituteEntityOptional.isPresent()) {
            // TODO : throw exception
            return null;
        }

        if (!Objects.isNull(request.getProgramId())) {
            CoachingProgramEntity coachingProgram =
                    coachingProgramRepository.findByProgramId(request.getProgramId()).orElse(null);

            if (Objects.isNull(coachingProgram)) {
                // TODO : throw exception
                return null;
            }
        }

        if (!Objects.isNull(request.getStreamId())) {
            Stream stream = streamRepository.findByStreamId(request.getStreamId());

            if (Objects.isNull(stream)) {
                // TODO : throw exception
                return null;
            }
        }

        CoachingExamEntity toSave =
                coachingExamTransformer.transform(request, existingCoachingExam);
        return coachingExamRepositoryNew.save(toSave);
    }
}
