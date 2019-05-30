package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

@Data
@ToString
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingInstitute {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @NotNull
    @Field("official_name")
    @JsonProperty("official_name")
    private String officialName;

    @Field("about_institute")
    @JsonProperty("about_institute")
    private String aboutInstitute;

    @JsonProperty("centers")
    private List<CoachingCenter> coachingCenters;

    @Field("official_address")
    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @Field("gallery")
    @JsonProperty("gallery")
    private Gallery gallery;

    @Field("cover_image")
    @JsonProperty("cover_image")
    private String coverImage;

    @Field("streams_prepared_for")
    @JsonProperty("streams_prepared_for")
    private List<String> streamsPreparedFor;

    @Field("contacts")
    @JsonProperty("contacts")
    private Map<String, String> contacts;

    @Field("exams_prepared_for")
    @JsonProperty("exams_prepared_for")
    private List<Long> examsPreparedFor;

    @Field("courses_available")
    @JsonProperty("courses_available")
    private List<Long> coursesAvailable;

    @Field("testimonials")
    @JsonProperty("testimonials")
    private List<Testimonial> testimonials;

    @Field("faqs")
    @JsonProperty("faqs")
    private List<Faq> faqs;

    @Field("students_selected")
    @JsonProperty("students_selected")
    private List<StudentSelected> studentsSelected;

    @Field("top_rank_achieved")
    @JsonProperty("top_rank_achieved")
    private List<TopRankAchieved> topRankAchieved;

    @Field("scholarships")
    @JsonProperty("scholarships")
    private List<Object> scholarships;

    @Field("exam_centers")
    @JsonProperty("exam_centers")
    private List<Integer> examCenters;

    @Field("sample_papers")
    @JsonProperty("sample_papers")
    private List<SamplePaper> samplePapers;

    @Field("daily_practice_tests")
    @JsonProperty("daily_practice_tests")
    private List<Integer> dailyPracticeTests;

    @Field("establishment_year")
    @JsonProperty("establishment_year")
    private Integer establishmentYear;

    @Field("no_of_selected_student")
    @JsonProperty("no_of_selected_student")
    private Integer noOfSelectedStudent;

    @Field("no_of_faculty")
    @JsonProperty("no_of_faculty")
    private Integer noOfFaculty;

    @Field("media")
    @JsonProperty("media")
    private Map<String, String> media;

    @Field("facilities")
    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("active")
    @Field("active")
    private boolean active;

}
