package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.embedded.CoachingCourseImportantDate;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
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
@NoArgsConstructor
@Document(collection = "coaching_course")
public class CoachingCourseEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    ObjectId id;

    @Field("faqs")
    List<Faq> faqs;

    @Field("course_id")
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

    @Field("auxiliary_exam_ids")
    private List<Long> auxiliaryExamIds;

    @Field("duration_type")
    private DurationType durationType;

    @Field("duration")
    private Integer duration;

    @Field("eligibility")
    private String eligibility;

    @Field("info")
    private String info;

    @Field("description")
    private String description;

    @Field("features")
    private List<Long> features;

    @Field("price")
    private Double price;

    @Field("currency")
    private Currency currency;

    @Field("level")
    private Level level;

    @Field("language")
    private Language language;

    @Field("syllabus")
    private String syllabus;

    @Field("brochure")
    private String brochure;

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

    @Field("classroom_teacher_student_ratio")
    public Integer classroomTeacherStudentRatio;

}
