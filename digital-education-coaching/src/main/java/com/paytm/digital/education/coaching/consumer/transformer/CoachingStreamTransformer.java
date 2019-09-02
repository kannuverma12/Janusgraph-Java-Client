package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourse;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class CoachingStreamTransformer {

    public static List<CoachingCourse> convertCoachingCourseEntityToDto(
            List<CoachingCourse> coachingCourseList,
            List<CoachingCourseEntity> coachingCourseEntityList) {
        if (!CollectionUtils.isEmpty(coachingCourseEntityList)) {
            for (CoachingCourseEntity coachingCourseEntity : coachingCourseEntityList) {
                CoachingCourse coachingCourse = CoachingCourse.builder()
                        .courseId(coachingCourseEntity.getCourseId())
                        .courseName(coachingCourseEntity.getName())
                        .courseType(
                                Objects.nonNull(coachingCourseEntity.getCourseType())
                                        ? coachingCourseEntity.getCourseType().getText() :
                                        null)
                        .duration(coachingCourseEntity.getDuration())
                        .eligibility(coachingCourseEntity.getEligibility())
                        .build();
                coachingCourseList.add(coachingCourse);
            }
        }
        return coachingCourseList;
    }

    public static List<CoachingInstitute> convertCoachingInstituteEntityToDto(
            List<CoachingInstitute> coachingInstituteList,
            List<CoachingInstituteEntity> coachingInstituteEntityList) {
        if (!CollectionUtils.isEmpty(coachingInstituteEntityList)) {
            for (CoachingInstituteEntity coachingInstituteEntity : coachingInstituteEntityList) {
                CoachingInstitute coachingInstitute = CoachingInstitute.builder()
                        .id(coachingInstituteEntity.getInstituteId())
                        .name(coachingInstituteEntity.getBrandName())
                        .image(coachingInstituteEntity.getLogo())
                        .build();
                coachingInstituteList.add(coachingInstitute);
            }
        }
        return coachingInstituteList;
    }
}
