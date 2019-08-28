package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Cutoff;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.Gender;
import com.paytm.digital.education.explore.response.dto.common.CutOff;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;
import com.paytm.digital.education.explore.service.CutoffService;
import com.paytm.digital.education.explore.service.helper.GenderAndCasteGroupHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.explore.utility.FieldsRetrievalUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.EXISTS;
import static com.paytm.digital.education.constant.ExploreConstants.CASTEGROUP;
import static com.paytm.digital.education.constant.ExploreConstants.COURSE_CLASS;
import static com.paytm.digital.education.constant.ExploreConstants.CUTOFF;
import static com.paytm.digital.education.constant.ExploreConstants.CUTOFF_CASTE_GROUP;
import static com.paytm.digital.education.constant.ExploreConstants.CUTOFF_EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.CUTOFF_GENDER;
import static com.paytm.digital.education.constant.ExploreConstants.EXAMS_ACCEPTED;
import static com.paytm.digital.education.constant.ExploreConstants.GENDER;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.OTHER_CATEGORIES;
import static com.paytm.digital.education.enums.EducationEntity.COURSE;

import static com.paytm.digital.education.enums.Gender.OTHERS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_CUTOFF_EXISTS;

@Slf4j
@AllArgsConstructor
@Service

public class CutoffServiceImpl implements CutoffService {

    private CommonMongoRepository     commonMongoRepository;
    private GenderAndCasteGroupHelper genderAndCasteGroupHelper;

    private Map<String, Map<String, Object>> genderCategoryMap;

    @PostConstruct
    private void setGenderCategoryMap() {
        genderCategoryMap = genderAndCasteGroupHelper.getGenderAndCasteGroupMap();
    }

    @Cacheable(value = "cutoff_search")
    public List<CutoffSearchResponse> searchCutOffs(long instituteId, long examId, Gender gender,
            String casteGroup, String fieldGroup) {

        List<String> projectionFields = commonMongoRepository
                .getFieldsByGroup(Course.class, fieldGroup);
        if (projectionFields != null) {
            Map<String, ArrayList<String>> allFields =
                    FieldsRetrievalUtil.getFormattedFields(projectionFields, COURSE_CLASS);
            ArrayList<String> courseProjectionFields = allFields.get(COURSE.name().toLowerCase());
            if (Objects.nonNull(casteGroup) && casteGroup.equals(OTHER_CATEGORIES)) {
                casteGroup = null;
            }
            if (Objects.nonNull(gender) && gender.equals(OTHERS)) {
                gender = null;
            }
            List<Course> courseAndCutoffs = commonMongoRepository
                    .findAll(buildQueryObject(instituteId, examId, gender, casteGroup),
                            Course.class,
                            courseProjectionFields, AND);
            if (courseAndCutoffs.isEmpty()) {
                throw new NotFoundException(NO_CUTOFF_EXISTS,
                        NO_CUTOFF_EXISTS.getExternalMessage());
            }
            return buildResponse(courseAndCutoffs, gender, casteGroup, examId);
        } else {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
    }

    private Map<String, Object> buildQueryObject(long instituteId, long examId, Gender gender,
            String casteGroup) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, instituteId);
        queryObject.put(CUTOFF_EXAM_ID, examId);
        queryObject.put(CUTOFF_CASTE_GROUP, casteGroup);
        queryObject.put(CUTOFF_GENDER, gender);
        return queryObject;
    }

    private List<CutoffSearchResponse> buildResponse(List<Course> courses, Gender gender,
            String casteGroup, long examId) {
        List<CutoffSearchResponse> response = new ArrayList<>();
        for (Course course : courses) {
            CutoffSearchResponse individualResponse = new CutoffSearchResponse();
            individualResponse.setCourseDuration(course.getCourseDuration());
            individualResponse.setCourseId(course.getCourseId());
            individualResponse.setCourseNameOfficial(course.getCourseNameOfficial());
            individualResponse.setUrlDisplayKey(
                    CommonUtil.convertNameToUrlDisplayName(course.getCourseNameOfficial()));
            individualResponse.setMasterBranch(course.getMasterBranch());
            individualResponse.setCourseLevel(course.getCourseLevel());
            individualResponse.setCutOffs(getCutOffList(course.getCutoffs(), gender, casteGroup,
                    examId));
            response.add(individualResponse);
        }
        return response;
    }

    private List<CutOff> getCutOffList(List<Cutoff> cutoffs, Gender gender, String casteGroup,
            long examId) {
        List<CutOff> responseCutoffs = new ArrayList<>();
        Collections.sort(cutoffs,
                Comparator.comparingInt(Cutoff::getYear).reversed());
        int year = 0;
        for (Cutoff cutoff : cutoffs) {
            if (cutoff.getExamId() == examId) {
                String cutoffCasteGroup = cutoff.getCasteGroup();
                Gender cutoffGender = cutoff.getGender();
                if (Objects.equals(cutoffCasteGroup, casteGroup)
                        && Objects.equals(cutoffGender, gender) && (year == 0
                        || year == cutoff.getYear())) {
                    if (year == 0) {
                        year = cutoff.getYear();
                    }
                    CutOff individualCutOff = new CutOff();
                    individualCutOff.setCasteGroup(cutoffCasteGroup);
                    individualCutOff.setCutoffType(cutoff.getCutoffType());
                    individualCutOff.setGender(cutoffGender);
                    individualCutOff.setExamId(cutoff.getExamId());
                    individualCutOff.setValue(cutoff.getFinalValue());
                    individualCutOff.setLocation(cutoff.getLocation());
                    individualCutOff.setYear(cutoff.getYear());
                    responseCutoffs.add(individualCutOff);
                }
            }
        }
        return responseCutoffs;
    }

    @Cacheable(value = "cutoff_get_list")
    public ExamAndCutOff getSearchList(long instituteId, long examId) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(INSTITUTE_ID, instituteId);
        queryParams.put(CUTOFF_EXAM_ID, examId);
        Map<String, Boolean> existsQuery = new HashMap<>();
        existsQuery.put(EXISTS, true);
        queryParams.put(EXAMS_ACCEPTED, existsQuery);
        ExamAndCutOff genderCasteList = getGenderCasteGroupList(queryParams);
        return genderCasteList;
    }

    private ExamAndCutOff getGenderCasteGroupList(Map<String, Object> queryParams) {
        ExamAndCutOff examAndCutOff = new ExamAndCutOff();
        List<String> projectionFields = Arrays.asList(CUTOFF);
        List<Course> courses = commonMongoRepository
                .findAll(queryParams, Course.class,
                        projectionFields, AND);
        Map<Gender, String> genders = new HashMap<>();
        Map<String, String> casteGroups = new HashMap<>();
        Map<String, Object> genderMap = genderCategoryMap.get(GENDER);
        Map<String, Object> casteGroupMap = genderCategoryMap.get(CASTEGROUP);
        for (Course course : courses) {
            for (Cutoff cutoff : course.getCutoffs()) {
                String caste = cutoff.getCasteGroup();
                Gender gender = cutoff.getGender();
                if (Objects.nonNull(gender)) {
                    genders.put(gender, (String) genderMap.get(gender.toString()));
                } else {
                    genders.put(OTHERS, (String) genderMap.get(OTHERS.toString()));
                }
                if (StringUtils.isNotBlank(caste)) {
                    casteGroups.put(caste, (String) casteGroupMap.get(caste));
                } else {
                    casteGroups.put(OTHER_CATEGORIES, (String) casteGroupMap.get(OTHER_CATEGORIES));
                }
            }
        }
        if (!casteGroups.isEmpty() && !(casteGroups.size() == 1 && casteGroups.entrySet().iterator()
                .next().getKey()
                .equals(OTHER_CATEGORIES))) {
            examAndCutOff.setCasteGroups(casteGroups);
        }
        if (!genders.isEmpty() && !(genders.size() == 1 && genders.entrySet().iterator().next()
                .getKey()
                .equals(OTHERS))) {
            examAndCutOff.setGenders(genders);
        }
        return examAndCutOff;
    }
}
