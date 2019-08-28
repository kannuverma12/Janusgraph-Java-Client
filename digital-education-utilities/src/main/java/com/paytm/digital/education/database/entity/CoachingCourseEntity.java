package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.embedded.CoachingCourseFeature;
import com.paytm.digital.education.database.embedded.CoachingCourseImportantDate;
import com.paytm.digital.education.database.embedded.CoachingCourseSessionDetails;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coaching_course")
public class CoachingCourseEntity {

    @Id
    @Field("_id")
    ObjectId id;

    @Field("faqs")
    List<Faq> faqs;

    @Field("course_id")
    @Setter
    private Long courseId;

    @Field("name")
    private String name;

    @Field("coaching_institute_id")
    private Long coachingInstituteId;

    @Field("course_type")
    private CourseType courseType;

    @Field("stream")
    private Long stream;

    @Field("primary_exam_ids")
    private List<Long> primaryExamIds;

    @Field("auxiliary_exam_ids")
    private List<Long> auxiliaryExamIds;

    @Field("duration")
    private String duration;

    @Field("eligibility")
    private String eligibility;

    @Field("info")
    private String info;

    @Field("description")
    private String description;

    @Field("price")
    private Double price;

    @Field("currency")
    private Currency currency;

    @Field("level")
    private Level level;

    @Field("language")
    private String language;

    @Field("syllabus")
    private String syllabus;

    @Field("brochure")
    private String brochure;

    @Field("important_dates")
    private List<CoachingCourseImportantDate> importantDates;

    @Field("global_priority")
    private Integer globalPriority;

    @Field("session_details")
    private List<CoachingCourseSessionDetails> sessionDetails;

    @Field("features")
    private List<CoachingCourseFeature> features;

    @Field("is_scholarship_available")
    private Boolean isScholarshipAvailable;

    @Field("test_count")
    private Integer testCount;

    @Field("test_duration")
    private Integer testDuration;

    @Field("test_series_duration")
    private Integer testSeriesDuration;

    @Field("types_of_results")
    private String typesOfResults;

    @Field("is_doubt_solving_session_available")
    private Boolean isDoubtSolvingSessionAvailable;

    @Field("number_of_books")
    private Integer numberOfBooks;

    @Field("delivery_schedule")
    private String deliverySchedule;

    @Field("inclusions")
    private List<String> inclusions;

    @Field("how_to_use")
    private List<String> howToUse;

    @Field("is_enabled")
    private Boolean isEnabled = Boolean.TRUE;

    @Field("created_at")
    @CreatedDate
    @JsonIgnore
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime updatedAt;

    @Field("priority")
    private Integer priority;
}
