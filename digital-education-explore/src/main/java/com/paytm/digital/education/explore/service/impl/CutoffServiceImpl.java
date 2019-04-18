package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Cutoff;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.response.dto.common.CutOff;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;
import com.paytm.digital.education.explore.service.CutoffService;
import com.paytm.digital.education.explore.utility.FieldsRetrievalUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mongodb.QueryOperators.AND;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_CLASS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_CASTE_GROUP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_CUTOFF_EXISTS;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_LIST_EXISTS;

@Slf4j
@AllArgsConstructor
@Service
public class CutoffServiceImpl implements CutoffService {

    private CommonMongoRepository commonMongoRepository;

    @Cacheable(value = "cutoff_search")
    public List<CutoffSearchResponse> searchCutOffs(long instituteId, long examId, Gender gender,
            String casteGroup, String fieldGroup) {
        List<String> projectionFields = commonMongoRepository
                .getFieldsByGroup(Course.class, fieldGroup);
        if (projectionFields != null) {
            Map<String, ArrayList<String>> allFields =
                    FieldsRetrievalUtil.getFormattedFields(projectionFields, COURSE_CLASS);
            ArrayList<String> courseProjectionFields = allFields.get(COURSE.name().toLowerCase());
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
            individualResponse.setMasterBranch(course.getMasterBranch());
            individualResponse.setCourseLevel(course.getCourseLevel());
            individualResponse.setCutOffs(getCutOffs(course.getCutoffs(), gender, casteGroup,
                    examId));
            response.add(individualResponse);
        }
        return response;
    }

    private List<CutOff> getCutOffs(List<Cutoff> cutoffs, Gender gender, String casteGroup,
            long examId) {
        List<CutOff> responseCutoffs = new ArrayList<>();
        for (Cutoff cutoff : cutoffs) {
            if (cutoff.getCasteGroup().equals(casteGroup) && cutoff
                    .getGender().equals(gender)
                    && cutoff.getExamId() == examId) {
                CutOff individualCutOff = new CutOff();
                individualCutOff.setCasteGroup(cutoff.getCasteGroup());
                individualCutOff.setCutoffType(cutoff.getCutoffType());
                individualCutOff.setGender(cutoff.getGender());
                individualCutOff.setExamId(cutoff.getExamId());
                individualCutOff.setValue(cutoff.getFinalValue());
                individualCutOff.setMeritListType(cutoff.getMeritListType());
                individualCutOff.setYear(cutoff.getYear());
                responseCutoffs.add(individualCutOff);
            }
        }
        return responseCutoffs;
    }

    @Cacheable(value = "cutoff_get_list")
    public ExamAndCutOff getSearchList(long instituteId, long examId) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(INSTITUTE_ID, instituteId);
        queryParams.put(CUTOFF_EXAM_ID, examId);
        ExamAndCutOff genderCasteList = getGenderCasteGroupList(queryParams);
        if (genderCasteList.getCasteGroups() == null) {
            throw new NotFoundException(NO_LIST_EXISTS,
                    NO_LIST_EXISTS.getExternalMessage());
        }
        return genderCasteList;
    }

    private ExamAndCutOff getGenderCasteGroupList(Map<String, Object> queryParams) {
        ExamAndCutOff examAndCutOff = new ExamAndCutOff();
        List<String> projectionFields = Arrays.asList(CUTOFF);
        List<Course> courses = commonMongoRepository
                .findAll(queryParams, Course.class,
                        projectionFields, AND);
        Set<Gender> genders = new HashSet<>();
        Set<String> casteGroups = new HashSet<>();
        for (Course course : courses) {
            for (Cutoff cutoff : course.getCutoffs()) {
                genders.add(cutoff.getGender());
                casteGroups.add(cutoff.getCasteGroup());
            }
        }
        if (!casteGroups.isEmpty()) {
            examAndCutOff.setCasteGroups(new ArrayList<>(casteGroups));
            examAndCutOff.setGenders(new ArrayList<>(genders));
        }
        return examAndCutOff;
    }
}
