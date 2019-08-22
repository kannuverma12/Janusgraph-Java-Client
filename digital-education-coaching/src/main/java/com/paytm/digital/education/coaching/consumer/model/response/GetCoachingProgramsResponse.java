package com.paytm.digital.education.coaching.consumer.model.response;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgram;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetCoachingProgramsResponse {

    private List<CoachingProgram> coachingPrograms;
}
