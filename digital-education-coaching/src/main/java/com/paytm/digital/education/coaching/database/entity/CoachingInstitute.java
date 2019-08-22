package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Deprecated
@ToString
//@Document("coaching_institute")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingInstitute extends ResponseDto {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("institute_name")
    @JsonProperty("institute_name")
    private String instituteName;

    @Field("brand_name")
    @JsonProperty("brand_name")
    private String brandName;

    @Field("about_institute")
    @JsonProperty("about_institute")
    private String aboutInstitute;

    @Field("official_address")
    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @Field("gallery")
    @JsonProperty("gallery")
    private List<Media> gallery;

    @Field("cover_image")
    @JsonProperty("cover_image")
    private String coverImage;

    @Field("logo")
    @JsonProperty("logo")
    private String logo;

    @Field("streams_prepared_for")
    @JsonProperty("streams_prepared_for")
    private List<String> streamsPreparedFor;

    @Field("contacts")
    @JsonProperty("contacts")
    private Map<String, String> contacts;

    @Field("exams_prepared_for")
    @JsonProperty("exams_prepared_for")
    private List<Long> examsPreparedFor;

    @Field("courses_type_available")
    @JsonProperty("courses_type_available")
    private List<CourseType> coursesTypeAvailable;

    @Field("students_selected")
    @JsonProperty("students_selected")
    private List<StudentSelected> studentsSelected;

    @Field("top_rank_achieved")
    @JsonProperty("top_rank_achieved")
    private List<TopRankAchieved> topRankAchieved;

    @Field("scholarship_matrix")
    @JsonProperty("scholarship_matrix")
    private String scholarshipMatrix;

    @Field("scholarship_exam")
    @JsonProperty("scholarship_exam")
    private String scholarshipExam;

    @Field("exam_centers")
    @JsonProperty("exam_centers")
    private List<Integer> examCenters;

    @Field("establishment_year")
    @JsonProperty("establishment_year")
    private Integer establishmentYear;

    @Field("facilities")
    @JsonProperty("facilities")
    private Map<String, String> facilities;

    @JsonProperty("active")
    @Field("active")
    private Boolean active;

    @Field("created_at")
    @JsonProperty("created_at")
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    private Date updatedAt;

    @Field("brochure")
    @JsonProperty("brochure")
    private String brochure;

    @Field("steps_to_apply")
    @JsonProperty("steps_to_apply")
    private String stepsToApply;

    @Field("city_state_presence")
    @JsonProperty("city_state_presence")
    private String cityStatePresence;

    @Field("keyhighlights")
    @JsonProperty("keyhighlights")
    private Map<Integer, KeyHighlight> keyhighlights;
}
