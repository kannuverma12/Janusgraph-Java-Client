package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CoachingExamManagerService {

    @Autowired
    private CoachingExamServiceNew coachingExamService;

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    @Autowired
    private StreamService streamService;

    public CoachingExamDTO insertCoachingExam(CoachingExamDataRequest request) {

        CoachingInstituteEntity existingCoachingInstitute =
                coachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitute)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        //
        //        if (!Objects.isNull(request.getProgramId())) {
        //            CoachingProgramEntity coachingProgram =
        //                    programRepository.findByProgramId(request.getProgramId()).orElse(null);
        //
        //            if (Objects.isNull(coachingProgram)) {
        //                // TODO : throw exception
        //                return null;
        //            }
        //        }

        StreamEntity existingStreamEntity = streamService.findByStreamId(request.getStreamId());
        if (Objects.isNull(existingStreamEntity)) {
            throw new InvalidRequestException("StreamEntity id not present");
        }

        return CoachingExamDTO.builder()
                .coachingExamId(coachingExamService.insertCoachingExam(request).getCoachingExamId())
                .build();
    }

    public CoachingExamDTO updateCoachingExam(CoachingExamDataRequest request) {

        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        //        if (!Objects.isNull(request.getProgramId())) {
        //            CoachingProgramEntity coachingProgram =
        //                    programRepository.findByProgramId(request.getProgramId()).orElse(null);
        //
        //            if (Objects.isNull(coachingProgram)) {
        //                // TODO : throw exception
        //                return null;
        //            }
        //        }


        StreamEntity existingStreamEntity = streamService.findByStreamId(request.getStreamId());
        if (Objects.isNull(existingStreamEntity)) {
            throw new InvalidRequestException("StreamEntity id not present");
        }

        return CoachingExamDTO.builder()
                .coachingExamId(coachingExamService.updateCoachingExam(request).getCoachingExamId())
                .build();
    }
}
