package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.embedded.CoachingCourseImportantDate;
import com.paytm.digital.education.enums.CTAViewType;
import com.paytm.digital.education.enums.CourseCover;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "coaching_course")
@CompoundIndex(def = "{'paytm_product_id':1, 'merchant_product_id':1}", unique = true, name = "course_unique")
public class CoachingCourseEntity extends Base {

    private static final long serialVersionUID = 2708311073409061214L;

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("course_id")
    @Indexed(unique = true)
    private Long courseId;

    @Field("name")
    private String name;

    @Field("coaching_institute_id")
    private Long coachingInstituteId;

    @Field("course_type")
    private CourseType courseType;

    @Field("stream_ids")
    private List<Long> streamIds;

    @Field("primary_exam_ids")
    private List<Long> primaryExamIds;

    @Field("duration_type")
    private DurationType durationType;

    @Field("duration")
    private Integer duration;

    @Field("validity_type")
    private DurationType validityType;

    @Field("validity")
    private Integer validity;

    @Field("eligibility")
    private String eligibility;

    @Field("info")
    private String info;

    @Field("description")
    private String description;

    @Field("original_price")
    private Double originalPrice;

    @Field("discounted_price")
    private Double discountedPrice;

    @Field("level")
    private CourseLevel courseLevel;

    @Field("course_cover")
    private CourseCover courseCover;

    @Field("language")
    private String language;

    @Field("syllabus")
    private String syllabus;

    @Field("important_dates")
    private List<CoachingCourseImportantDate> importantDates;

    @Field("how_to_use_1")
    private String howToUse1;

    @Field("how_to_use_2")
    private String howToUse2;

    @Field("how_to_use_3")
    private String howToUse3;

    @Field("how_to_use_4")
    private String howToUse4;

    @Field("is_certificate_available")
    private Boolean isCertificateAvailable;

    @Field("is_doubt_solving_forum_available")
    private Boolean isDoubtSolvingForumAvailable;

    @Field("is_progress_analysis_available")
    private Boolean isProgressAnalysisAvailable;

    @Field("is_rank_analysis_available")
    private Boolean isRankAnalysisAvailable;

    @Field("course_features")
    private List<Long> courseFeatureIds;

    @Field("classroom_teacher_student_ratio")
    public String classroomTeacherStudentRatio;

    @Field("test_count")
    public Integer testCount;

    @Field("test_duration")
    public Integer testDuration;

    @Field("test_question_count")
    public Integer testQuestionCount;

    @Field("test_practice_paper_count")
    public Integer testPracticePaperCount;

    @Field("distance_learning_books_count")
    public Integer distanceLearningBooksCount;

    @Field("distance_learning_solved_paper_count")
    public Integer distanceLearningSolvedPaperCount;

    @Field("distance_learning_assignment_count")
    public Integer distanceLearningAssignmentsCount;

    @Field("elearning_lecture_count")
    public Integer elearningLectureCount;

    @Field("elearning_lecture_duration")
    public Integer elearningLectureDuration;

    @Field("elearning_online_test_count")
    public Integer elearningOnlineTestCount;

    @Field("elearning_practice_paper_count")
    public Integer elearningPracticePaperCount;

    @Field("classroom_lecture_count")
    public Integer classroomLectureCount;

    @Field("classroom_lecture_duration")
    public Integer classroomLectureDuration;

    @Field("classroom_test_count")
    public Integer classroomTestCount;

    @Field("sgst")
    private String sgst;

    @Field("cgst")
    private String cgst;

    @Field("igst")
    private String igst;

    @Field("tcs")
    private String tcs;

    @Field("merchant_product_id")
    private String merchantProductId;

    @Field("paytm_product_id")
    private Long paytmProductId;

    @Field("is_dynamic")
    private Boolean isDynamic;

    @Field("cta_info")
    private Map<CTAViewType, List<Long>> ctaInfo;

    @Field("is_onboarded")
    private Boolean isOnboarded;
}
