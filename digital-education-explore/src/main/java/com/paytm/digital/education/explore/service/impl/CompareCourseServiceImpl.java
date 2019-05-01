package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.CourseFee;
import com.paytm.digital.education.explore.database.entity.Cutoff;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.response.dto.common.CutOff;
import com.paytm.digital.education.explore.response.dto.detail.CompareCourseDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareCourses;
import com.paytm.digital.education.explore.service.CompareCourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GENERAL;
import static com.paytm.digital.education.explore.enums.Gender.FEMALE;
import static com.paytm.digital.education.explore.enums.Gender.MALE;
import static com.paytm.digital.education.explore.enums.Gender.OTHERS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;

@Slf4j
@Service
@AllArgsConstructor
public class CompareCourseServiceImpl implements CompareCourseService {
    private CommonMongoRepository commonMongoRepository;
    private CompareServiceImpl    compareService;

    public List<CompareCourseDetail> compareCourses(List<Long> courseList, String fieldGroup,
            List<String> fields) {
        List<String> projectionFields = commonMongoRepository
                .getFieldsByGroup(Course.class, fieldGroup);
        if (Objects.isNull(projectionFields)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
        List<Course> courses = commonMongoRepository
                .getEntityFieldsByValuesIn(COURSE_ID, courseList, Course.class, projectionFields);
        Set<Long> examIds = courses.stream().filter(c -> Objects.nonNull(c.getExamsAccepted()))
                .flatMap(c -> c.getExamsAccepted().stream())
                .collect(Collectors.toSet());
        return buildCompareCourseResponse(courses, examIds);
    }

    private List<CompareCourseDetail> buildCompareCourseResponse(List<Course> courses, Set<Long> examIds) {
        Map<Long, String> examDataMap = getExamDataMap(examIds);
        List<CompareCourseDetail> compareCourseList = new ArrayList<>();
        for (Course course : courses) {
            CompareCourseDetail compareCourseDetail = new CompareCourseDetail();
            compareCourseDetail.setCourseDurationInMonths(course.getCourseDuration());
            compareCourseDetail.setCourseName(course.getCourseNameOfficial());
            compareCourseDetail.setCourseId(course.getCourseId());
            compareCourseDetail.setEligibilityCriteria(course.getEligibilityCriteria());
            compareCourseDetail.setTotalIntake(course.getSeatsAvailable());
            List<CourseFee> courseFees = course.getCourseFees();
            if (Objects.nonNull(courseFees) && !courseFees.isEmpty()) {
                compareCourseDetail.setCourseFee(getCourseFeeForCompare(courseFees));
            }
            compareCourseDetail.setCourseLevel(course.getCourseLevel());
            List<Long> examsAcceptedIds = course.getExamsAccepted();
            if (Objects.nonNull(examsAcceptedIds)) {
                compareCourseDetail
                        .setExamsAccepted(getExamNames(examsAcceptedIds, examDataMap));
                List<Cutoff> cutoffs = course.getCutoffs();
                if (Objects.nonNull(cutoffs)) {
                    compareCourseDetail
                            .setCutoffs(getCutoffsForCompare(cutoffs, examDataMap));
                }
            }
            compareCourseList.add(compareCourseDetail);
        }
        return compareCourseList;
    }

    /*
     ** Get the fee for the general category
     */
    private Integer getCourseFeeForCompare(List<CourseFee> courseFees) {
        for (CourseFee courseFee : courseFees) {
            String casteGroup = courseFee.getCasteGroup();
            if ((Objects.isNull(casteGroup) && Objects.nonNull(courseFee.getFee()))
                    || (casteGroup.equalsIgnoreCase(GENERAL))) {
                return courseFee.getFee();
            }
        }
        return null;
    }

    /*
     ** Find the cutoffs that need to be displayed in the compare course
     */
    private List<CutOff> getCutoffsForCompare(List<Cutoff> cutoffs, Map<Long, String> examDataMap) {
        List<CutOff> cutoffList = new ArrayList<>();
        Map<Long, Map<Gender, Cutoff>> minCutoffsDataMap = getMinCutOffs(cutoffs, examDataMap);
        for (long examId : minCutoffsDataMap.keySet()) {
            Map<Gender, Cutoff> minCutoffs = minCutoffsDataMap.get(examId);
            Cutoff othersCutoff = minCutoffs.get(OTHERS);
            if (Objects.nonNull(othersCutoff)) {
                setResponseCutoffData(cutoffList, othersCutoff,
                        examDataMap.get(othersCutoff.getExamId()));
            } else {
                Cutoff maleCutoff = minCutoffs.get(MALE);
                Cutoff femaleCutoff = minCutoffs.get(FEMALE);
                if (Objects.nonNull(maleCutoff)) {
                    setResponseCutoffData(cutoffList, maleCutoff,
                            examDataMap.get(maleCutoff.getExamId()));
                }
                if (Objects.nonNull(femaleCutoff)) {
                    setResponseCutoffData(cutoffList, femaleCutoff,
                            examDataMap.get(femaleCutoff.getExamId()));
                }
            }
        }
        return cutoffList;
    }

    private void setResponseCutoffData(List<CutOff> cutoffList, Cutoff cutoff, String examName) {
        CutOff cutOff = new CutOff();
        BeanUtils.copyProperties(cutoff, cutOff);
        cutOff.setExamShortName(examName);
        cutOff.setValue(cutoff.getFinalValue());
        if (!cutoff.getGender().equals(OTHERS)) {
            cutOff.setGender(cutoff.getGender());
        }
        cutoffList.add(cutOff);
    }

    /*
     ** Find the minimum cutoffs based on the following rules:
     * 1. Cutoffs should be from the most recent year as per your individual course data.
     * 2. It should be based on gender if gender exists in all the cutoffs of a course
     * 3. If there are any cutoffs where gender does not exists, priority should be given to it
     *    in the calculation irrespective of whether it contains the minimum cutoffs or not,
     *    provided it should be minimum of all the cutoffs where gender does not exists.
     * 4. Cutoffs should be of GENERAL caste group
     */
    private Map<Long, Map<Gender, Cutoff>> getMinCutOffs(List<Cutoff> cutoffs,
            Map<Long, String> examDataMap) {
        Map<Long, Map<Gender, Cutoff>> minCutoffMap = new HashMap<>();
        for (Cutoff cutoff : cutoffs) {
            Long examId = cutoff.getExamId();
            Gender gender = Objects.nonNull(cutoff.getGender()) ? cutoff.getGender() : OTHERS;
            String casteGroup = cutoff.getCasteGroup();
            if (Objects.nonNull(examDataMap.get(examId)) && (Objects
                    .isNull(casteGroup) || casteGroup.equalsIgnoreCase(GENERAL))) {
                Map<Gender, Cutoff> minCutoffs = minCutoffMap.get(examId);
                if (Objects.nonNull(minCutoffs)) {
                    Cutoff genderMinCutoff = minCutoffs.get(gender);
                    if (Objects.nonNull(genderMinCutoff)) {
                        int cutOffYear = cutoff.getYear();
                        int genderMinCutoffYear = genderMinCutoff.getYear();
                        if (cutOffYear > genderMinCutoffYear || (
                                cutOffYear == genderMinCutoffYear
                                        && cutoff.getFinalValue() < genderMinCutoff
                                        .getFinalValue())) {
                            cutoff.setGender(gender);
                            minCutoffs.put(gender, cutoff);
                        }
                    } else {
                        cutoff.setGender(gender);
                        minCutoffs.put(gender, cutoff);
                    }
                } else {
                    cutoff.setGender(gender);
                    Map<Gender, Cutoff> newGenderCutoff = new HashMap<>();
                    newGenderCutoff.put(gender, cutoff);
                    minCutoffMap.put(examId, newGenderCutoff);
                }

            }
        }
        return minCutoffMap;
    }

    private List<String> getExamNames(List<Long> examIds, Map<Long, String> examDataMap) {
        List<String> examNameList = examIds.stream()
                .map(id -> examDataMap.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return examNameList;
    }

    private Map<Long, String> getExamDataMap(Set<Long> examIds) {
        List<Exam> examList = compareService.getExamList(examIds);
        Map<Long, String> examData = new HashMap<>();
        for (Exam exam : examList) {
            examData.put(exam.getExamId(), exam.getExamShortName());
            if (Objects.nonNull(exam.getSubExams())) {
                for (SubExam subExam : exam.getSubExams()) {
                    examData.put(subExam.getId(), exam.getExamShortName());
                }
            }
        }
        return examData;
    }
}
