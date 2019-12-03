package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class FacultyDetail implements Serializable {
    @JsonProperty("total_teachers")
    private Integer totalTeachers;

    @JsonProperty("total_teachers_image_url")
    private String totalTeachersImageUrl;

    @JsonProperty("student_to_teacher_ratio")
    private String studentToTeacherRatio;

    @JsonProperty("no_of_trained_teachers")
    private Integer noOfTrainedTeachers;

    @JsonProperty("no_of_untrained_teachers")
    private Integer noOfUntrainedTeachers;

    @JsonProperty("student_to_teacher_ratio_image_url")
    private String studentToTeacherRatioImageUrl;

    public FacultyDetail(Integer totalTeachers, String studentToTeacherRatio) {
        this.totalTeachers = totalTeachers;
        this.studentToTeacherRatio = studentToTeacherRatio;
    }
}
