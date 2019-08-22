package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.database.embedded.CoachingProgramFeature;
import com.paytm.digital.education.database.embedded.CoachingProgramSessionDetails;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coaching_program")
public class CoachingProgramEntity {

    @Id
    @Field("_id")
    ObjectId id;

    @Field("faqs")
    List<Faq> faqs;

    @Field("program_id")
    private Long programId;

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
    // Duration in Months
    @Field("duration")
    private Integer    duration;

    @Field("eligibility")
    private String eligibility;

    @Field("info")
    private String info;

    @Field("description")
    private String description;

    @Field("price")
    private Integer price;

    @Field("level")
    private Level level;

    @Field("language")
    private String language;

    @Field("syllabus_and_brochure")
    private String syllabusAndBrochure;

    @Field("global_priority")
    private Integer globalPriority;

    @Field("session_details")
    private List<CoachingProgramSessionDetails> sessionDetails;

    @Field("features")
    private List<CoachingProgramFeature> features;

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
}
